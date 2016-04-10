package com.argo.redis;

import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dengyaming on 4/10/16.
 */
public class RedisSentinelBuket extends RedisSimpleBuket {

    static RedisSentinelBuket redisBuket = null;

    public RedisSentinelBuket() throws Exception {
    }

    public synchronized static RedisSentinelBuket getInstance() throws Exception {
        if (redisBuket == null){
            redisBuket = new RedisSentinelBuket();
        }

        return redisBuket;
    }

    /**
     * 初始化链接池(Sentinel集群版本)
     */
    @Override
    protected void initJedisPool() {
        Set<String> sets = new HashSet<>();
        sets.addAll(redisConfig.getSentinel().hosts);
        this.jedisPool = new JedisSentinelPool(redisConfig.getSentinel().master, sets, jedisPoolConfig, redisConfig.getTimeout(), redisConfig.getPasswd());
        logger.info("initJedisPool. {}", this.jedisPool);
    }
}
