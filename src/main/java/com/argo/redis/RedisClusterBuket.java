package com.argo.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.util.*;

/**
 * Created by dengyaming on 4/10/16.
 */
public class RedisClusterBuket extends RedisSimpleBuket {

    static RedisClusterBuket redisBuket = null;

    public RedisClusterBuket() throws Exception {
    }

    public synchronized static RedisClusterBuket getClusterInstance() throws Exception {
        if (redisBuket == null){
            redisBuket = new RedisClusterBuket();
        }

        return redisBuket;
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

    @Override
    public List<String> fromBytes(List<byte[]> lbs) {
        return super.fromBytes(lbs);
    }

    @Override
    public List<String> mget(String... keys) {
        List<byte[]> bytes = jedisCluster.mget(SafeEncoder.encodeMany(keys));
        List<String> ret = new ArrayList<String>();
        for (byte[] item : bytes){
            if (item != null) {
                ret.add(SafeEncoder.encode(item));
            }
        }
        return ret;
    }

    @Override
    public <T> List<T> mget(Class<T> clazz, String... keys) {
        List<T> ret = Collections.EMPTY_LIST;
        try {
            List<byte[]> bytes = jedisCluster.mget(SafeEncoder.encodeMany(keys));
            ret = getRedisBuffer().read(bytes, clazz);
            return ret;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return ret;
        }
    }

    @Override
    public String get(String key) {
        byte[] bytes = jedisCluster.get(SafeEncoder.encode(key));
        if (bytes == null){
            return null;
        }
        return SafeEncoder.encode(bytes);
    }

    @Override
    public <T> T get(Class<T> clazz, String key) {
        byte[] bytes = jedisCluster.get(SafeEncoder.encode(key));
        if (bytes == null){
            return null;
        }
        try {
            return getRedisBuffer().read(bytes, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> String set(String key, T value) {
        try {
            byte[] ds = getRedisBuffer().write(value);
            return jedisCluster.set(SafeEncoder.encode(key), ds);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> boolean set(List<String> keys, List<T> values) {
        try {
            for (int i = 0; i < keys.size(); i++) {
                byte[] ds = getRedisBuffer().write(values.get(i));
                jedisCluster.set(SafeEncoder.encode(keys.get(i)), ds);
            }
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getSet(String key, String value) {
        byte[] ret = jedisCluster.getSet(SafeEncoder.encode(key), SafeEncoder.encode(value));
        if (ret == null){
            return null;
        }
        return SafeEncoder.encode(ret);
    }

    @Override
    public <T> T getSet(Class<T> clazz, String key, T value) {
        try {
            byte[] ds = getRedisBuffer().write(value);
            byte[] ret = jedisCluster.getSet(SafeEncoder.encode(key), ds);
            if (ret == null){
                return null;
            }
            return getRedisBuffer().read(ret, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Long setnx(String key, String value) {
        return jedisCluster.setnx(SafeEncoder.encode(key), SafeEncoder.encode(value));
    }

    @Override
    public <T> Long setnx(String key, T value) {
        try {
            byte[] ds = getRedisBuffer().write(value);
            return jedisCluster.setnx(SafeEncoder.encode(key), ds);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return jedisCluster.setex(SafeEncoder.encode(key), seconds, SafeEncoder.encode(value));
    }

    @Override
    public <T> String setex(String key, int seconds, T value) {
        try {
            byte[] ds = getRedisBuffer().write(value);
            return jedisCluster.setex(SafeEncoder.encode(key), seconds, ds);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public long expire(String key, int timeout) {
        return jedisCluster.expire(SafeEncoder.encode(key), timeout);
    }

    @Override
    public long expireAt(String key, int timeout) {
        long ts = System.currentTimeMillis()/1000 + timeout;
        return jedisCluster.expireAt(SafeEncoder.encode(key), ts);
    }

    @Override
    public boolean exists(String key) {
        return jedisCluster.exists(SafeEncoder.encode(key));
    }

    @Override
    public boolean delete(String... keys) {
        return jedisCluster.del(SafeEncoder.encodeMany(keys)) > 0;
    }

    @Override
    public long incr(String key, Integer amount) {
        return jedisCluster.incrBy(SafeEncoder.encode(key), amount);
    }

    @Override
    public long decr(String key, Integer amount) {
        return jedisCluster.decrBy(SafeEncoder.encode(key), amount);
    }

    @Override
    public long hincr(String key, String field, Integer amount) {
        return jedisCluster.hincrBy(SafeEncoder.encode(key), SafeEncoder.encode(field), amount);
    }

    @Override
    public long hincr(String key, Map<String, Integer> nums) {
        byte[] bk = SafeEncoder.encode(key);
        for(String field : nums.keySet()){
            jedisCluster.hincrBy(bk, SafeEncoder.encode(field), nums.get(field));
        }
        return 1L;
    }

    @Override
    public long hdecr(String key, String field, Integer amount) {
        return jedisCluster.hincrBy(SafeEncoder.encode(key), SafeEncoder.encode(field), -1 * amount);
    }

    @Override
    public long hdecr(String key, Map<String, Integer> nums) {
        byte[] bk = SafeEncoder.encode(key);
        for(String field : nums.keySet()){
            jedisCluster.hincrBy(bk, SafeEncoder.encode(field), -1 * nums.get(field));
        }
        return 1L;
    }

    @Override
    public Map<String, Integer> hall(String key) {
        Map<byte[], byte[]> bs = jedisCluster.hgetAll(SafeEncoder.encode(key));
        Map<String, Integer> vals = new HashMap<String, Integer>();
        Iterator<byte[]> itor = bs.keySet().iterator();
        while (itor.hasNext()){
            byte[] k = itor.next();
            byte[] data = bs.get(k);
            int v = data == null ? 0 : Integer.parseInt(SafeEncoder.encode(data));
            vals.put(SafeEncoder.encode(k), v);
        }
        return vals;
    }

    @Override
    public boolean hrem(String key, String... fields) {
        byte[] bk = SafeEncoder.encode(key);
        return jedisCluster.hdel(bk, SafeEncoder.encodeMany(fields)) > 0;
    }

    @Override
    public long hset(String key, Map<String, Integer> nums) {
        byte[] bk = SafeEncoder.encode(key);
        for(String field : nums.keySet()){
            jedisCluster.hset(bk, SafeEncoder.encode(field), SafeEncoder.encode(String.valueOf(nums.get(field))));
        }
        return 1L;
    }

    @Override
    public Long rpush(String key, String... values) {
        return jedisCluster.rpush(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
    }

    @Override
    public <T> Long rpush(Class<T> clazz, String key, T... values) {
        try {
            byte[][] bytes = new byte[values.length][];
            for (int i = 0; i < values.length; i++) {
                bytes[i] = getRedisBuffer().write(values[i]);
            }
            return jedisCluster.rpush(SafeEncoder.encode(key), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Long lpush(String key, String... values) {
        return jedisCluster.lpush(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
    }

    @Override
    public <T> Long lpush(Class<T> clazz, String key, T... values) {
        try {
            byte[][] bytes = new byte[values.length][];
            for (int i = 0; i < values.length; i++) {
                bytes[i] = getRedisBuffer().write(values[i]);
            }
            return jedisCluster.lpush(SafeEncoder.encode(key), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Long llen(String key) {
        return jedisCluster.llen(SafeEncoder.encode(key));
    }

    @Override
    public List<String> lrange(String key, int page, int limit) {
        long start = (page - 1) * limit;
        long end = start + limit;
        List<byte[]> ls = jedisCluster.lrange(SafeEncoder.encode(key), start, end);
        List<String> ret = new ArrayList<String>();
        for (byte[] b : ls){
            if (b != null) {
                ret.add(SafeEncoder.encode(b));
            }
        }
        return ret;
    }

    @Override
    public <T> List<T> lrange(Class<T> clazz, String key, int page, int limit) {
        long start = (page - 1) * limit;
        long end = start + limit;
        List<byte[]> ls = jedisCluster.lrange(SafeEncoder.encode(key), start, end);
        List<T> ret = Collections.EMPTY_LIST;
        try {
            ret = getRedisBuffer().read(ls, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    @Override
    public String ltrim(String key, int start, int end) {
        return jedisCluster.ltrim(SafeEncoder.encode(key), start, end);
    }

    @Override
    public String lindex(String key, int index) {
        byte[] bs = jedisCluster.lindex(SafeEncoder.encode(key), index);
        if (bs == null){
            return null;
        }
        return new String(bs);
    }

    @Override
    public <T> T lindex(Class<T> clazz, String key, int index) {
        byte[] bs = jedisCluster.lindex(SafeEncoder.encode(key), index);
        if (bs == null){
            return null;
        }
        try {
            return getRedisBuffer().read(bs, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String lset(String key, int index, String value) {
        return jedisCluster.lset(SafeEncoder.encode(key), index, SafeEncoder.encode(value));
    }

    @Override
    public <T> String lset(Class<T> clazz, String key, int index, T value) {
        try {
            byte[] bytes = getRedisBuffer().write(value);
            return jedisCluster.lset(SafeEncoder.encode(key), index, bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Long lrem(String key, String value) {
        return jedisCluster.lrem(SafeEncoder.encode(key), 0, SafeEncoder.encode(value));
    }

    @Override
    public <T> Long lrem(Class<T> clazz, String key, T value) {
        try {
            byte[] bytes = getRedisBuffer().write(value);
            return jedisCluster.lrem(SafeEncoder.encode(key), 0, bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String lpop(String key) {
        byte[] bs = jedisCluster.lpop(SafeEncoder.encode(key));
        if (bs == null){
            return null;
        }
        return SafeEncoder.encode(bs);
    }

    @Override
    public <T> T lpop(Class<T> clazz, String key) {
        byte[] bs = jedisCluster.lpop(SafeEncoder.encode(key));
        if (bs == null){
            return null;
        }
        try {
            return getRedisBuffer().read(bs, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<String> lpop(String key, int limit) {
        byte[] bk = SafeEncoder.encode(key);
        List<String> resp = new ArrayList<String>();
        for (int i = 0; i < limit; i++) {
            byte[] bs = jedisCluster.lpop(bk);
            if (bs != null){
                resp.add(SafeEncoder.encode(bs));
            }

        }
        return resp;
    }

    @Override
    public <T> List<T> lpop(Class<T> clazz, String key, int limit) {
        byte[] bk = SafeEncoder.encode(key);
        List<T> resp = new ArrayList<T>();
        for (int i = 0; i < limit; i++) {
            byte[] bs = jedisCluster.lpop(bk);
            if (bs != null){
                try {
                    resp.add(getRedisBuffer().read(bs, clazz));
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    resp.add(null);
                }
            }

        }
        return resp;
    }

    @Override
    public String rpop(String key) {
        byte[] bs = jedisCluster.rpop(SafeEncoder.encode(key));
        if (bs == null){
            return null;
        }
        return SafeEncoder.encode(bs);
    }

    @Override
    public <T> T rpop(Class<T> clazz, String key) {
        byte[] bs = jedisCluster.rpop(SafeEncoder.encode(key));
        if (bs == null){
            return null;
        }
        try {
            return getRedisBuffer().read(bs, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<String> rpop(String key, int limit) {
        byte[] bk = SafeEncoder.encode(key);
        List<String> resp = new ArrayList<String>();
        for (int i = 0; i < limit; i++) {
            byte[] bs = jedisCluster.rpop(bk);
            if (bs != null){
                resp.add(SafeEncoder.encode(bs));
            }

        }
        return resp;
    }

    @Override
    public <T> List<T> rpop(Class<T> clazz, String key, int limit) {
        byte[] bk = SafeEncoder.encode(key);
        List<T> resp = new ArrayList<T>();
        for (int i = 0; i < limit; i++) {
            byte[] bs = jedisCluster.rpop(bk);
            if (bs != null){
                try {
                    resp.add(getRedisBuffer().read(bs, clazz));
                } catch (IOException e) {
                    resp.add(null);
                }
            }

        }
        return resp;
    }

    @Override
    public List<String> lsort(String key) {
        List<byte[]> lbs = jedisCluster.sort(SafeEncoder.encode(key));
        List<String> ret = new ArrayList<String>();
        for (byte[] b : lbs){
            if (b != null) {
                ret.add(SafeEncoder.encode(b));
            }
        }
        return ret;
    }

    @Override
    public Long lpushx(String key, String... values) {
        return jedisCluster.lpushx(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
    }

    @Override
    public <T> Long lpushx(Class<T> clazz, String key, T... values) {
        try {
            byte[][] bytes = new byte[values.length][];
            for (int i = 0; i < values.length; i++) {
                bytes[i] = getRedisBuffer().write(values[i]);
            }
            return jedisCluster.lpushx(SafeEncoder.encode(key), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0l;
        }
    }

    @Override
    public Long rpushx(String key, String... values) {
        return jedisCluster.rpushx(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
    }

    @Override
    public <T> Long rpushx(Class<T> clazz, String key, T... values) {
        try {
            byte[][] bytes = new byte[values.length][];
            for (int i = 0; i < values.length; i++) {
                bytes[i] = getRedisBuffer().write(values[i]);
            }
            return jedisCluster.rpushx(SafeEncoder.encode(key), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0l;
        }
    }

    @Override
    public List<String> blpop(int timeout, String... keys) {
        List<byte[]> bs = jedisCluster.blpop(timeout, SafeEncoder.encodeMany(keys));
        return fromBytes(bs);
    }

    @Override
    public <T> List<T> blpop(Class<T> clazz, int timeout, String... keys) {
        List<byte[]> bs = jedisCluster.blpop(timeout, SafeEncoder.encodeMany(keys));
        return fromBytes(clazz, bs);
    }

    @Override
    public List<String> brpop(int timeout, String... keys) {
        List<byte[]> bs = jedisCluster.brpop(timeout, SafeEncoder.encodeMany(keys));
        return fromBytes(bs);
    }

    @Override
    public <T> List<T> brpop(Class<T> clazz, int timeout, String... keys) {
        List<byte[]> bs = jedisCluster.brpop(timeout, SafeEncoder.encodeMany(keys));
        return fromBytes(clazz, bs);
    }

    @Override
    public Long sadd(String key, String... members) {
        return jedisCluster.sadd(SafeEncoder.encode(key), SafeEncoder.encodeMany(members));
    }

    @Override
    public Long sadd(String key, List<?> members) {
        byte[] bk = SafeEncoder.encode(key);
        for(Object v : members){
            jedisCluster.sadd(bk, SafeEncoder.encode(String.valueOf(v)));
        }
        return 1L;
    }

    @Override
    public Set<String> smembers(String key) {
        Set<byte[]> sk = jedisCluster.smembers(SafeEncoder.encode(key));
        return fromBytes(sk);
    }

    @Override
    public Long srem(String key, String... members) {
        return jedisCluster.srem(SafeEncoder.encode(key), SafeEncoder.encodeMany(members));
    }

    @Override
    public String spop(String key) {
        byte[] bytes = jedisCluster.spop(SafeEncoder.encode(key));
        if (bytes == null){
            return null;
        }
        return SafeEncoder.encode(bytes);
    }

    @Override
    public Long scard(String key) {
        return jedisCluster.scard(SafeEncoder.encode(key));
    }

    @Override
    public Boolean sismember(String key, String member) {
        return jedisCluster.sismember(SafeEncoder.encode(key), SafeEncoder.encode(member));
    }

    @Override
    public List<String> srandmember(String key, int count) {
        List<byte[]> lbs = jedisCluster.srandmember(SafeEncoder.encode(key), count);
        if (lbs == null){
            return Collections.emptyList();
        }
        return fromBytes(lbs);
    }

    @Override
    public List<String> ssort(String key) {
        List<byte[]> lbs = jedisCluster.sort(SafeEncoder.encode(key));
        return fromBytes(lbs);
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return jedisCluster.zadd(SafeEncoder.encode(key), score, SafeEncoder.encode(member));
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        Map<byte[], Double> ms = new HashMap<byte[], Double>();
        Iterator<String> itor = scoreMembers.keySet().iterator();
        while (itor.hasNext()){
            String k = itor.next();
            ms.put(SafeEncoder.encode(k), scoreMembers.get(k));
        }
        return jedisCluster.zadd(SafeEncoder.encode(key), ms);
    }

    @Override
    public Set<String> zrange(String key, int page, int limit) {
        long start = (page - 1) * limit;
        long end = start + limit;
        Set<byte[]> bs = jedisCluster.zrange(SafeEncoder.encode(key), start, end);
        return fromBytes(bs);
    }

    @Override
    public Set<String> zrevrange(String key, int page, int limit) {
        long start = (page - 1) * limit;
        long end = start + limit;
        Set<byte[]> bs = jedisCluster.zrevrange(SafeEncoder.encode(key), start, end);
        return fromBytes(bs);
    }

    @Override
    public Long zrem(String key, String... member) {
        return jedisCluster.zrem(SafeEncoder.encode(key), SafeEncoder.encodeMany(member));
    }

    @Override
    public Double zincrby(String key, double score, String member) {
        return jedisCluster.zincrby(SafeEncoder.encode(key), score, SafeEncoder.encode(member));
    }

    @Override
    public Long zrank(String key, String member) {
        return jedisCluster.zrank(SafeEncoder.encode(key), SafeEncoder.encode(member));
    }

    @Override
    public Long zrevrank(String key, String member) {
        return jedisCluster.zrevrank(SafeEncoder.encode(key), SafeEncoder.encode(member));
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, int page, int limit) {
        long start = (page - 1) * limit;
        long end = start + limit;
        Set<Tuple> bs = jedisCluster.zrangeWithScores(SafeEncoder.encode(key), start, end);
        return bs;
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, int page, int limit) {
        long start = (page - 1) * limit;
        long end = start + limit;
        return jedisCluster.zrevrangeWithScores(SafeEncoder.encode(key), start, end);
    }

    @Override
    public Long zcard(String key) {
        return jedisCluster.zcard(SafeEncoder.encode(key));
    }

    @Override
    public Double zscore(String key, String member) {
        return jedisCluster.zscore(SafeEncoder.encode(key), SafeEncoder.encode(member));
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return jedisCluster.zcount(SafeEncoder.encode(key), min, max);
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return jedisCluster.zcount(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        Set<byte[]> bs = jedisCluster.zrangeByScore(SafeEncoder.encode(key), min, max);
        return fromBytes(bs);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        Set<byte[]> bs = jedisCluster.zrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
        return fromBytes(bs);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double min, double max) {
        Set<byte[]> bs = jedisCluster.zrevrangeByScore(SafeEncoder.encode(key), min, max);
        return fromBytes(bs);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String min, String max) {
        Set<byte[]> bs = jedisCluster.zrevrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
        return fromBytes(bs);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return jedisCluster.zrangeByScoreWithScores(SafeEncoder.encode(key), min, max);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return jedisCluster.zrangeByScoreWithScores(SafeEncoder.encode(key), min, max);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return jedisCluster.zrangeByScoreWithScores(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return jedisCluster.zrevrangeByScoreWithScores(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
    }

    @Override
    public Long zremrangeByRank(String key, int start, int end) {
        return jedisCluster.zremrangeByRank(SafeEncoder.encode(key), start, end);
    }

    @Override
    public Long zremrangeByScore(String key, double start, double end) {
        return jedisCluster.zremrangeByScore(SafeEncoder.encode(key), start, end);
    }

    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        return jedisCluster.zremrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(start), SafeEncoder.encode(end));
    }
}
