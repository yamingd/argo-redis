package com.argo.redis;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class RedisTemplate implements Closeable {

    protected Logger logger = null;

    private boolean ALIVE = true;
    private boolean serverDown = false;

    private volatile boolean stopping = false;
    private RedisConfig redisConfig = null;

    private Pool<Jedis> jedisPool;
    private JedisPoolConfig jedisPoolConfig;

    protected MessagePack messagePack = new MessagePack();
    protected MonitorThread monitorThread;

    public RedisTemplate() throws Exception {
        logger = LoggerFactory.getLogger(this.getClass());

        RedisConfig.load();
        redisConfig = RedisConfig.instance;

        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.redisConfig.getMaxActive());
        jedisPoolConfig.setMaxIdle(this.redisConfig.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(this.redisConfig.getTimeout() * 1000);
        jedisPoolConfig.setTestOnBorrow(this.redisConfig.getTestOnBorrow());
        jedisPoolConfig.setTestWhileIdle(this.redisConfig.getTestWhileIdle());

        this.initJedisPool();

        monitorThread =  new MonitorThread();
        monitorThread.setDaemon(true);
        monitorThread.setName("RedisBucketMonitor");
        monitorThread.start();
    }

    @Override
    public void close() throws IOException {
        stopping = true;
        monitorThread.interrupt();
        if (null != this.jedisPool){
            this.jedisPool.close();
        }
    }

    public Pool<Jedis> getJedisPool() {
        return jedisPool;
    }

    protected void initJedisPool() {
        if (null == redisConfig.getSentinel() || !redisConfig.getSentinel().enabled) {
            this.jedisPool =
                    new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout(), redisConfig.getPasswd());
            logger.info("initJedisPool. {}", this.jedisPool);
        }else{
            Set<String> sets = new HashSet<>();
            sets.addAll(redisConfig.getSentinel().hosts);
            this.jedisPool = new JedisSentinelPool(redisConfig.getSentinel().master, sets, jedisPoolConfig, redisConfig.getTimeout(), redisConfig.getPasswd());
            logger.info("initJedisPool. {}", this.jedisPool);
        }
    }

    // 执行具体COMMAND
    public <T> T execute(final RedisCommand<T> action) {
        if (!ALIVE) {
            logger.error("Redis is Still Down.");
            return null;
        }
        Jedis conn = null;
        boolean error = false;
        try {
            int limit = 3;
            while (limit > 0) {
                try {
                    conn = getJedisPool().getResource();
                    limit = 0;
                } catch (Exception e) {
                    logger.error("Get Resource ERROR.", e);
                    limit --;
                }
                if (null != conn){
                    break;
                }
            }
            if (null == conn){
                serverDown = true;
                error = true;
                logger.error("Execute Redis Command ERROR. Could not get a resource from the pool");
                return null;
            }else {
                return action.execute(conn);
            }
        } catch (Exception e) {
            serverDown = true;
            error = true;
            logger.error("Execute Redis Command ERROR.", e);
            return null;
        } finally {
            if (conn != null) {
                try {
                    if (error) {
                        this.returnBorkenConnection(conn);
                    } else {
                        this.returnConnection(conn);
                    }
                } catch (Exception e) {
                    logger.error(
                        "Error happen when return jedis to pool, try to close it directly.",
                        e);
                    if (conn.isConnected()) {
                        try {
                            try {
                                conn.quit();
                            } catch (Exception e1) {
                            }
                            conn.disconnect();
                        } catch (Exception e2) {

                        }
                    }
                }
            }
        }
    }

    public String info() {
        return this.execute(new RedisCommand<String>() {
            public String execute(Jedis conn) throws Exception {
                return conn.info();
            }
        });
    }

    /**
     * 关闭数据库连接
     *
     * @param jedis
     */
    private void returnConnection(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }

    /**
     * 关闭错误连接
     *
     * @param jedis
     */
    private void returnBorkenConnection(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }

    public String getServerName() {
        return redisConfig.getHost();
    }

    private class MonitorThread extends Thread {
        @Override
        public void run() {
            int sleepTime = redisConfig.getAliveCheck() * 1000;
            int baseSleepTime = 1000;
            while (!stopping) {

                logger.info("{}", jedisPool.toString());

                try {
                    // 30秒执行监听
                    int n = sleepTime / baseSleepTime;
                    for (int i = 0; i < n; i++) {
                        if (serverDown) {// 检查到异常，立即进行检测处理
                            break;
                        }
                        try {
                            Thread.sleep(baseSleepTime);
                        } catch (InterruptedException e) {
                            break;
                        }
                        if (stopping){
                            break;
                        }
                    }
                    if (stopping){
                        break;
                    }
                    // 连续做3次连接获取
                    int errorTimes = 0;
                    for (int i = 0; i < 3 && !stopping; i++) {
                        try {
                            Jedis jedis = getJedisPool().getResource();
                            if (jedis == null) {
                                errorTimes++;
                                continue;
                            }
                            returnConnection(jedis);
                            break;
                        } catch (Exception e) {
                            if (stopping){
                                break;
                            }
                            logger.error("redis链接错误", e);
                            errorTimes++;
                        }
                    }
                    if (stopping){
                        break;
                    }
                    if (errorTimes == 3) {// 3次全部出错，表示服务器出现问题
                        ALIVE = false;
                        serverDown = true; // 只是在异常出现第一次进行跳出处理，后面的按异常检查时间进行延时处理
                        logger.error("redis[{}] 服务器连接不上", getServerName());
                        // 修改休眠时间为5秒，尽快恢复服务
                        sleepTime = 5000;
                    } else {
                        if (ALIVE == false) {
                            ALIVE = true;
                            // 修改休眠时间为30秒，服务恢复
                            sleepTime = redisConfig.getAliveCheck() * 1000;
                            logger.info("redis[{}] 服务器恢复正常", getServerName());
                        }
                        serverDown = false;
                        Jedis jedis = getJedisPool().getResource();
                        logger.info("redis[{}] 当前记录数：{}", getServerName(), jedis.dbSize());
                        returnConnection(jedis);
                    }
                } catch (Exception e) {
                    if (stopping){
                        break;
                    }
                    logger.error("redis错误", e);
                }
            }

            logger.info("RedisClient has been exit. {}", new Date());

        }
    }
}
