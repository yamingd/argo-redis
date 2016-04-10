package com.argo.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dengyaming on 4/10/16.
 */
public class RedisClusterBuket extends RedisBuket {

    static RedisClusterBuket redisBuket = null;

    public RedisClusterBuket() throws Exception {
    }

    public synchronized static JedisCluster getClusterInstance() throws Exception {
        if (redisBuket == null){
            redisBuket = new RedisClusterBuket();
        }

        return redisBuket.getJedisCluster();
    }

    private JedisCluster jedisCluster;

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    /**
     * 初始化链接池(Sentinel集群版本)
     */
    @Override
    protected void initJedisPool() {

        Set<HostAndPort> sets = new HashSet<>();
        for (String str : redisConfig.getCluster().hosts) {
            String[] tmp = str.split(":");
            HostAndPort hostAndPort = new HostAndPort(tmp[0], Integer.parseInt(tmp[1]));
            sets.add(hostAndPort);
        }

        jedisCluster = new JedisCluster(sets, redisConfig.getTimeout(), this.jedisPoolConfig);
        logger.info("initJedisPool. {}", this.jedisPool);
    }

    @Override
    public <T> T execute(RedisCommand<T> action) {
        logger.error("Unsupported function.");
        return null;
    }
}
