package com.argo.redis;

import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dengyaming on 4/10/16.
 */
public interface RedisBuket {
    /**
     * 把byte[]转化为String
     * @param lbs byte[]数组
     * @return
     */
    List<String> fromBytes(List<byte[]> lbs);

    /**
     * 把byte[]转化为String
     * @param lbs byte[]数组
     * @return
     */
    Set<String> fromBytes(Set<byte[]> lbs);

    /**
     * 把byte[]转化为类实例
     * @param clazz
     * @param lbs
     * @param <T>
     * @return
     */
    <T> List<T> fromBytes(Class<T> clazz, List<byte[]> lbs);

    /**
     * 批量获取, 返回String数组
     * @param keys 缓存keys
     * @return List
     */
    List<String> mget(String... keys);

    /**
     * 批量获取, 返回类实例数组
     * @param clazz 类型
     * @param keys 缓存keys
     * @param <T> 返回类型
     * @return List
     */
    <T> List<T> mget(Class<T> clazz, String... keys);

    /**
     * 获取某Key的缓存字符串
     * @param key 缓存key
     * @return String
     */
    String get(String key);

    /**
     * 获取某Key的缓存类实例
     * @param clazz 类型
     * @param key 缓存key
     * @param <T> 类型
     * @return T
     */
    <T> T get(Class<T> clazz, String key);

    /**
     * 用key-value 缓存数据
     * @param key 缓存key
     * @param value 缓存value
     * @param <T> 数据类型
     * @return 操作结果代码
     */
    <T> String set(String key, T value);

    /**
     * 批量缓存数据
     * @param keys 缓存keys
     * @param values 缓存values
     * @param <T> 数据类型
     * @return true or false
     */
    <T> boolean set(List<String> keys, List<T> values);

    /**
     * 缓存某key的值, 并返回旧的值
     * @param key 缓存key
     * @param value 新值
     * @return String 旧值
     */
    String getSet(String key, String value);

    /**
     * 缓存某Key的值, 并返回旧的值
     * @param clazz 数据类型
     * @param key 缓存key
     * @param value 缓存值
     * @param <T> 类型
     * @return T 旧数据
     */
    <T> T getSet(Class<T> clazz, String key, T value);

    /**
     * 缓存某Key的值, 仅且在Key不存在的情况下
     * @param key 缓存key
     * @param value 缓存数据
     * @return Long
     */
    Long setnx(String key, String value);

    /**
     * 缓存某Key的值, 仅且在Key不存在的情况下
     * @param key 缓存Key
     * @param value 数据
     * @param <T> 数据类型
     * @return Long
     */
    <T> Long setnx(String key, T value);

    /**
     * 缓存某Key的数据, 同时设置过期时间秒数
     * @param key 缓存Key
     * @param seconds 过期秒数
     * @param value 数据
     * @return String
     */
    String setex(String key, int seconds, String value);

    /**
     * 缓存某Key的数据, 同时设置过期时间秒数
     * @param key 缓存Key
     * @param seconds 过期秒数
     * @param value 数据
     * @param <T> 数据类型
     * @return String
     */
    <T> String setex(String key, int seconds, T value);

    /**
     * 设置某Key的过期秒数
     * @param key 缓存Key
     * @param timeout 过期秒数
     * @return long
     */
    long expire(String key, int timeout);
    /**
     * 设置某Key的过期秒数
     * @param key 缓存Key
     * @param timeout 过期秒数
     * @return long
     */
    long expireAt(String key, int timeout);
    /**
     * 判断某Key是否存在
     * @param key 缓存Key
     * @return true or false
     */
    boolean exists(String key);

    /**
     * 删除缓存Key的数据
     * @param keys 缓存key
     * @return true or false
     */
    boolean delete(String... keys);

    /**
     * 递增某Key的Value(int, long型)
     * @param key 缓存key
     * @param amount 偏移量
     * @return long 新值
     */
    long incr(String key, Integer amount);

    /**
     * 递减某Key的Value(int, long型)
     * @param key 缓存key
     * @param amount 偏移量
     * @return long 新值
     */
    long decr(String key, Integer amount);

    /**
     * 递增HASH表里的某Key的Value
     * @param key 缓存Key
     * @param field HASH表字段名
     * @param amount 偏移量
     * @return long 新值
     */
    long hincr(String key, String field, Integer amount);

    /**
     * 递减HASH表里的某Key的Value
     * @param key 缓存Key
     * @param nums HASH表值
     * @return long
     */
    long hincr(String key, Map<String, Integer> nums);

    /**
     * 递减HASH表里的某Key的Value
     * @param key 缓存Key
     * @param field HASH表字段名
     * @param amount 偏移量
     * @return long
     */
    long hdecr(String key, String field, Integer amount);

    /**
     * 递减HASH表里的某Key的Value
     * @param key 缓存Key
     * @param nums HASH表值
     * @return long
     */
    long hdecr(String key, Map<String, Integer> nums);

    /**
     * 根据Key获取HASH表的数据
     * @param key 缓存Key
     * @return Map
     */
    Map<String, Integer> hall(String key);

    /**
     * 删除某HASH表的字段
     * @param key 缓存Key
     * @param fields 字段名
     * @return true or false
     */
    boolean hrem(String key, String... fields);

    /**
     * 设置缓存Key的HASH表数据
     * @param key 缓存Key
     * @param nums HASH表数据
     * @return long
     */
    long hset(String key, Map<String, Integer> nums);

    /**
     * 从List尾部添加数据
     * @param key 缓存Key
     * @param values 数据
     * @return Long
     */
    Long rpush(String key, String... values);

    /**
     * 从List尾部添加数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param values 数据
     * @param <T> 数据类型
     * @return Long
     */
    <T> Long rpush(Class<T> clazz, String key, T... values);

    /**
     * 从List头部添加数据
     * @param key 缓存Key
     * @param values 数据
     * @return Long
     */
    Long lpush(String key, String... values);

    /**
     * 从List头部添加数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param values 数据
     * @param <T> 数据类型
     * @return Long
     */
    <T> Long lpush(Class<T> clazz, String key, T... values);

    /**
     * 计算List长度
     * @param key 缓存Key
     * @return 长度
     */
    Long llen(String key);

    /**
     * 从List按分页取出数据
     * @param key 缓存Key
     * @param page 页码(从1开始)
     * @param limit 每次取出的数据个数
     * @return List
     */
    List<String> lrange(String key, int page, int limit);

    /**
     * 从List按分页取出数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param page 页码(从1开始)
     * @param limit 每次取出的数据个数
     * @param <T> 数据类型
     * @return List
     */
    <T> List<T> lrange(Class<T> clazz, String key, int page, int limit);

    /**
     * 从List中移除数据, 仅保留[start, end]区间的数据
     * @param key 缓存key
     * @param start 区间开始, 从0开始
     * @param end 结束
     * @return String
     */
    String ltrim(String key, int start, int end);

    /**
     * 从List中取出第i个数据
     * @param key 缓存key
     * @param index 数据位置, 从0开始
     * @return String
     */
    String lindex(String key, int index);

    /**
     * 从List中取出第i个数据
     * @param clazz 数据类型
     * @param key 缓存key
     * @param index 数据位置, 从0开始
     * @param <T> 数据类型
     * @return T
     */
    <T> T lindex(Class<T> clazz, String key, int index);

    /**
     * 在List中设置第i个数据
     * @param key 缓存key
     * @param index 数据位置, 从0开始
     * @param value 数据
     * @return 操作状态码
     */
    String lset(String key, int index, String value);

    /**
     * 在List中设置第i个数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param index 数据位置, 从0开始
     * @param value 数据
     * @param <T>
     * @return 操作状态码
     */
    <T> String lset(Class<T> clazz, String key, int index, T value);

    /**
     * 从List中删除数据
     * @param key 缓存key
     * @param value 数据
     * @return Long
     */
    Long lrem(String key, String value);

    /**
     * 从List中删除数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param value 数据
     * @param <T>
     * @return Long
     */
    <T> Long lrem(Class<T> clazz, String key, T value);

    /**
     * 从List的头部取出数据, 并从List中移除该数据
     * @param key 缓存key
     * @return String
     */
    String lpop(String key);

    /**
     * 从List的头部取出数据, 并从List中移除该数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param <T>
     * @return T
     */
    <T> T lpop(Class<T> clazz, String key);

    /**
     * 从List的头部取出数据, 并从List中移除该数据
     * @param key 数据类型
     * @param limit 取出的数据的个数
     * @return List
     */
    List<String> lpop(String key, int limit);

    /**
     * 从List的头部取出数据, 并从List中移除该数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param limit 取出的数据的个数
     * @param <T>
     * @return List
     */
    <T> List<T> lpop(Class<T> clazz, String key, int limit);

    /**
     * 从List的尾部取出数据, 并从List中移除该数据
     * @param key 缓存key
     * @return String
     */
    String rpop(String key);

    /**
     * 从List的尾部取出数据, 并从List中移除该数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param <T>
     * @return T
     */
    <T> T rpop(Class<T> clazz, String key);

    /**
     * 从List的尾部取出数据, 并从List中移除该数据
     * @param key 缓存Key
     * @param limit 取出的数据的个数
     * @return List
     */
    List<String> rpop(String key, int limit);

    /**
     * 从List的尾部取出数据, 并从List中移除该数据
     * @param clazz 数据类型
     * @param key 缓存Key
     * @param limit 取出的数据的个数
     * @param <T>
     * @return List
     */
    <T> List<T> rpop(Class<T> clazz, String key, int limit);

    /**
     * 对List中的数据(int, long, float, double类型)排序(升序)
     * @param key 缓存key
     * @return List
     */
    List<String> lsort(String key);

    /**
     *
     * @param key
     * @param values
     * @return
     */
    Long lpushx(String key, String... values);

    /**
     *
     * @param clazz
     * @param key
     * @param values
     * @param <T>
     * @return
     */
    <T> Long lpushx(Class<T> clazz, String key, T... values);

    /**
     *
     * @param key
     * @param values
     * @return
     */
    Long rpushx(String key, String... values);

    /**
     *
     * @param clazz
     * @param key
     * @param values
     * @param <T>
     * @return
     */
    <T> Long rpushx(Class<T> clazz, String key, T... values);

    /**
     * 从List的头部取出数据, 调用会等待到List中有数据
     * @param timeout 等待时间(秒)
     * @param keys 缓存Keys
     * @return List
     */
    List<String> blpop(int timeout, String... keys);

    /**
     * 从List的头部取出数据, 调用会等待到List中有数据
     * @param clazz 数据类型
     * @param timeout 等待时间(秒)
     * @param keys 缓存Keys
     * @param <T>
     * @return List
     */
    <T> List<T> blpop(Class<T> clazz, int timeout, String... keys);

    /**
     * 从List的尾部取出数据, 调用会等待到List中有数据
     * @param timeout 等待时间(秒)
     * @param keys 缓存Keys
     * @return List
     */
    List<String> brpop(int timeout, String... keys);

    /**
     * 从List的头部取出数据, 调用会等待到List中有数据
     * @param clazz 数据类型
     * @param timeout 等待时间(秒)
     * @param keys 缓存Keys
     * @param <T>
     * @return List
     */
    <T> List<T> brpop(Class<T> clazz, int timeout, String... keys);

    /**
     * 往SET里添加数据
     * @param key 缓存Keys
     * @param members 数据
     * @return Long
     */
    Long sadd(String key, String... members);

    /**
     * 往SET里添加数据
     * @param key 缓存Keys
     * @param members 数据
     * @return Long
     */
    Long sadd(String key, List<?> members);

    /**
     * 读取SET结构的数据
     * @param key 缓存Key
     * @return Set
     */
    Set<String> smembers(String key);

    /**
     * 在SET里移除数据
     * @param key 缓存Key
     * @param members 目标数据
     * @return
     */
    Long srem(String key, String... members);

    /**
     * 随机从SET里取出一个数据, 并从SET中移除
     * @param key 缓存Key
     * @return String
     */
    String spop(String key);

    /**
     * 计算SET的长度
     * @param key 缓存Key
     * @return Long
     */
    Long scard(String key);

    /**
     * 判断元素是否包含在SET里
     * @param key 缓存Key
     * @param member 元素数据
     * @return true or false
     */
    Boolean sismember(String key, String member);

    /**
     * 随机从SET里取出count个数据, 但不从SET中移除
     * @param key 缓存Key
     * @param count 数据个数
     * @return List
     */
    List<String> srandmember(String key, int count);

    /**
     * 对SET里的数据排序
     * @param key 缓存Key
     * @return List
     */
    List<String> ssort(String key);

    /**
     * 往有序SET里添加元素
     * @param key 缓存Key
     * @param score 元素分数(排序依据)
     * @param member 元素
     * @return Long
     */
    Long zadd(String key, double score, String member);

    /**
     * 往有序SET里添加元素
     * @param key 缓存Key
     * @param scoreMembers 元素集合
     * @return Long
     */
    Long zadd(String key, Map<String, Double> scoreMembers);

    /**
     * 从有序SET里按页取出元素(升序)
     * @param key 缓存Key
     * @param page 页码(从1开始)
     * @param limit 元素个数
     * @return Set
     */
    Set<String> zrange(String key, int page, int limit);

    /**
     * 从有序SET里按页取出元素(倒序)
     * @param key 缓存Key
     * @param page 页码(从1开始)
     * @param limit 元素个数
     * @return Set
     */
    Set<String> zrevrange(String key, int page, int limit);

    /**
     * 从有序SET里移除元素
     * @param key 缓存Key
     * @param member 元素
     * @return Long
     */
    Long zrem(String key, String... member);

    /**
     * 递增有序SET里元素的分数
     * @param key 缓存Key
     * @param score 递增的分数
     * @param member 元素
     * @return Double
     */
    Double zincrby(String key, double score, String member);

    /**
     * 返回有序SET里元素的排行(升序)
     * @param key 缓存Key
     * @param member 元素
     * @return Long
     */
    Long zrank(String key, String member);

    /**
     * 返回有序SET里元素的排行(倒序)
     * @param key 缓存Key
     * @param member 元素
     * @return Long
     */
    Long zrevrank(String key, String member);

    /**
     * 从有序SET里按页返回元素(升序)
     * @param key 缓存Key
     * @param page 页码(从1开始)
     * @param limit 返回的元素个数
     * @return Set of Tuple
     */
    Set<Tuple> zrangeWithScores(String key, int page, int limit);

    /**
     * 从有序SET里按页返回元素(倒序)
     * @param key 缓存Key
     * @param page 页码(从1开始)
     * @param limit 返回的元素个数
     * @return Set of Tuple
     */
    Set<Tuple> zrevrangeWithScores(String key, int page, int limit);

    /**
     * 计算有序SET的元素个数
     * @param key
     * @return Long
     */
    Long zcard(String key);

    /**
     * 读取有序SET某元素的分数
     * @param key 缓存Key
     * @param member 元素
     * @return Double
     */
    Double zscore(String key, String member);

    /**
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Long zcount(String key, double min, double max);

    /**
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Long zcount(String key, String min, String max);

    /**
     * 从有序SET里按[min, max]返回元素(升序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<String> zrangeByScore(String key, double min, double max);

    /**
     * 从有序SET里按[min, max]返回元素(升序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<String> zrangeByScore(String key, String min, String max);

    /**
     * 从有序SET里按[min, max]返回元素(倒序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<String> zrevrangeByScore(String key, double min, double max);
    /**
     * 从有序SET里按[min, max]返回元素(倒序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<String> zrevrangeByScore(String key, String min, String max);

    /**
     * 从有序SET里按[min, max]返回元素(升序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);
    /**
     * 从有序SET里按[min, max]返回元素(倒序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);
    /**
     * 从有序SET里按[min, max]返回元素(升序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max);
    /**
     * 从有序SET里按[min, max]返回元素(倒序)
     * @param key 缓存Key
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min);
    /**
     * 从有序SET里按[start, end]移除元素
     * @param key 缓存Key
     * @param start 开始位置
     * @param end 结束位置
     * @return Long
     */
    Long zremrangeByRank(String key, int start, int end);
    /**
     * 从有序SET里按[start, end]移除元素
     * @param key 缓存Key
     * @param start 开始分数
     * @param end 结束分数
     * @return Long
     */
    Long zremrangeByScore(String key, double start, double end);
    /**
     * 从有序SET里按[start, end]移除元素
     * @param key 缓存Key
     * @param start 开始分数
     * @param end 结束分数
     * @return Long
     */
    Long zremrangeByScore(String key, String start, String end);
}
