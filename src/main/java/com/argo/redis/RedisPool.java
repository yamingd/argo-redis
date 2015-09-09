package com.argo.redis;

import com.google.common.base.MoreObjects;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Protocol;
import redis.clients.util.JedisURIHelper;
import redis.clients.util.Pool;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/3
 * Time: 20:02
 */
public class RedisPool extends Pool<BinaryJedis> {

    public RedisPool(final GenericObjectPoolConfig poolConfig, final String host) {
        this(poolConfig, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT,
                null, Protocol.DEFAULT_DATABASE, null);
    }

    public RedisPool(String host, int port) {
        this(new GenericObjectPoolConfig(), host, port,
                Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null);
    }

    public RedisPool(final String host) {
        URI uri = URI.create(host);
        if (uri.getScheme() != null && uri.getScheme().equals("redis")) {
            String h = uri.getHost();
            int port = uri.getPort();
            String password = JedisURIHelper.getPassword(uri);
            int database = 0;
            Integer dbIndex = JedisURIHelper.getDBIndex(uri);
            if (dbIndex != null) {
                database = dbIndex.intValue();
            }
            this.internalPool = new GenericObjectPool<BinaryJedis>(
                    new BinaryJedisFactory(h, port, Protocol.DEFAULT_TIMEOUT,
                            password, database, null),
                    new GenericObjectPoolConfig());
        } else {
            this.internalPool = new GenericObjectPool<BinaryJedis>(new BinaryJedisFactory(
                    host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT,
                    null, Protocol.DEFAULT_DATABASE, null),
                    new GenericObjectPoolConfig());
        }
    }

    public RedisPool(final URI uri) {
        this(new GenericObjectPoolConfig(), uri, Protocol.DEFAULT_TIMEOUT);
    }

    public RedisPool(final URI uri, final int timeout) {
        this(new GenericObjectPoolConfig(), uri, timeout);
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port, int timeout, final String password) {
        this(poolConfig, host, port, timeout, password,
                Protocol.DEFAULT_DATABASE, null);
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, final int port) {
        this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null,
                Protocol.DEFAULT_DATABASE, null);
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, final int port, final int timeout) {
        this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE,
                null);
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port, int timeout, final String password,
                     final int database) {
        this(poolConfig, host, port, timeout, password, database, null);
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port, int timeout, final String password,
                     final int database, final String clientName) {
        super(poolConfig, new BinaryJedisFactory(host, port, timeout, password,
                database, clientName));
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig, final URI uri) {
        this(poolConfig, uri, Protocol.DEFAULT_TIMEOUT);
    }

    public RedisPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                     final int timeout) {
        super(poolConfig, new BinaryJedisFactory(uri.getHost(), uri.getPort(),
                timeout, JedisURIHelper.getPassword(uri),
                JedisURIHelper.getDBIndex(uri), null));
    }

    @Override
    public BinaryJedis getResource() {
        BinaryJedis jedis = super.getResource();
        return jedis;
    }

//    public void returnBrokenResource(final Jedis resource) {
//        if (resource != null) {
//            returnBrokenResourceObject(resource);
//        }
//    }
//
//    public void returnResource(final Jedis resource) {
//        if (resource != null) {
//            resource.resetState();
//            returnResourceObject(resource);
//        }
//    }

    public int getNumActive() {
        if (this.internalPool == null || this.internalPool.isClosed()) {
            return -1;
        }

        return this.internalPool.getNumActive();
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper h = MoreObjects.toStringHelper(this);
        h.add("NumActive", this.getNumActive());
        h.add("BorrowedCount", this.internalPool.getBorrowedCount());
        h.add("ReturnedCount", this.internalPool.getReturnedCount());
        h.add("CreatedCount", this.internalPool.getCreatedCount());
        h.add("DestroyedCount", this.internalPool.getDestroyedCount());
        h.add("MeanActiveTimeMillis", this.internalPool.getMeanActiveTimeMillis());
        h.add("MeanIdleTimeMillis", this.internalPool.getMeanIdleTimeMillis());
        h.add("MeanBorrowWaitTimeMillis", this.internalPool.getMeanBorrowWaitTimeMillis());
        h.add("MaxBorrowWaitTimeMillis", this.internalPool.getMaxBorrowWaitTimeMillis());
        return h.toString();
    }
}
