package com.argo.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("ALL")
public class RedisSimpleBuket extends RedisTemplate implements RedisBuket {

    static RedisBuket redisBuket = null;

    public RedisSimpleBuket() throws Exception {
    }

    public synchronized static RedisBuket getInstance() throws Exception {
        if (redisBuket == null){
            redisBuket = new RedisSimpleBuket();
        }

        return redisBuket;
    }

    /**
     * 转化为String
     * @param lbs
     * @return
     */
    @Override
    public List<String> fromBytes(List<byte[]> lbs){
        List<String> ret = new ArrayList<String>();
        if (lbs == null){
            return ret;
        }
        for (byte[] bs : lbs){
            if (bs != null) {
                try {
                    ret.add(SafeEncoder.encode(bs));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    ret.add(null);
                }
            }
        }
        return ret;
    }

    /**
     * 转化为String
     * @param lbs
     * @return
     */
    @Override
    public Set<String> fromBytes(Set<byte[]> lbs){
        Set<String> ret = new HashSet<>();
        if (lbs == null){
            return ret;
        }
        for (byte[] bs : lbs){
            if (bs != null) {
                try {
                    ret.add(SafeEncoder.encode(bs));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    ret.add(null);
                }
            }
        }
        return ret;
    }

    /**
     * 转化为对象
     * @param clazz
     * @param lbs
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> fromBytes(Class<T> clazz, List<byte[]> lbs){
        List<T> ret = Collections.EMPTY_LIST;
        try {
            ret = getRedisBuffer().read(lbs, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    @Override
    public List<String> mget(final String... keys){
		return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
                List<byte[]> bytes = conn.mget(SafeEncoder.encodeMany(keys));
                List<String> ret = new ArrayList<String>();
                for (byte[] item : bytes){
                    if (item != null) {
                        ret.add(SafeEncoder.encode(item));
                    }
                }
                return ret;
			}
		});
	}

    @Override
    public <T> List<T> mget(final Class<T> clazz, final String... keys){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final Jedis conn) throws Exception {
                List<byte[]> bytes = conn.mget(SafeEncoder.encodeMany(keys));
                return getRedisBuffer().read(bytes, clazz);
            }
        });
    }

	@Override
    public String get(final String key){
		return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				byte[] bytes = conn.get(SafeEncoder.encode(key));
                if (bytes == null){
                    return null;
                }
                return SafeEncoder.encode(bytes);
			}
		});
	}


    @Override
    public <T> T get(final Class<T> clazz, final String key){
        return this.execute(new RedisCommand<T>(){
            public T execute(final Jedis conn) throws Exception {
                byte[] bytes = conn.get(SafeEncoder.encode(key));
                if (bytes == null){
                    return null;
                }
                return getRedisBuffer().read(bytes, clazz);
            }
        });
    }

    @Override
    public <T> String set(final String key, final T value){
        return this.execute(new RedisCommand<String>(){
            public String execute(final Jedis conn) throws Exception {
                byte[] ds = getRedisBuffer().write(value);
                return conn.set(SafeEncoder.encode(key), ds);
            }
        });
    }

    @Override
    public <T> boolean set(final List<String> keys, final List<T> values){
        return this.execute(new RedisCommand<Boolean>(){
            public Boolean execute(final Jedis conn) throws Exception {
                for (int i = 0; i < keys.size(); i++) {
                    byte[] ds = getRedisBuffer().write(values.get(i));
                    conn.set(SafeEncoder.encode(keys.get(i)), ds);
                }
                return true;
            }
        });
    }

	@Override
    public String getSet(final String key, final String value){
		return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				byte[] ret = conn.getSet(SafeEncoder.encode(key), SafeEncoder.encode(value));
                if (ret == null){
                    return null;
                }
                return SafeEncoder.encode(ret);
			}
		});
	}

    @Override
    public <T> T getSet(final Class<T> clazz, final String key, final T value){
        return this.execute(new RedisCommand<T>(){
            public T execute(final Jedis conn) throws Exception {
                byte[] ds = getRedisBuffer().write(value);
                byte[] ret = conn.getSet(SafeEncoder.encode(key), ds);
                if (ret == null){
                    return null;
                }
                return getRedisBuffer().read(ret, clazz);
            }
        });
    }

    @Override
    public Long setnx(final String key, final String value){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.setnx(SafeEncoder.encode(key), SafeEncoder.encode(value));
			}
		});
    }

    @Override
    public <T> Long setnx(final String key, final T value){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                byte[] ds = getRedisBuffer().write(value);
                return conn.setnx(SafeEncoder.encode(key), ds);
            }
        });
    }

    @Override
    public String setex(final String key, final int seconds, final String value){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.setex(SafeEncoder.encode(key), seconds, SafeEncoder.encode(value));
			}
		});
    }

    @Override
    public <T> String setex(final String key, final int seconds, final T value){
        return this.execute(new RedisCommand<String>(){
            public String execute(final Jedis conn) throws Exception {
                byte[] ds = getRedisBuffer().write(value);
                return conn.setex(SafeEncoder.encode(key), seconds, ds);
            }
        });
    }


	@Override
    public long expire(final String key, final int timeout){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.expire(SafeEncoder.encode(key), timeout);
			}
		});
	}

    @Override
    public long expireAt(String key, int timeout) {
        long ts = System.currentTimeMillis() / 1000 + timeout;
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                return conn.expireAt(SafeEncoder.encode(key), ts);
            }
        });
    }

    @Override
    public boolean exists(final String key){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.exists(SafeEncoder.encode(key));
			}
		});
	}

	@Override
    public boolean delete(final String... keys){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.del(SafeEncoder.encodeMany(keys)) > 0;
			}
		});
	}

	@Override
    public long incr(final String key, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.incrBy(SafeEncoder.encode(key), amount);
			}
		});
	}

	@Override
    public long decr(final String key, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.decrBy(SafeEncoder.encode(key), amount);
			}
		});
	}

	@Override
    public long hincr(final String key, final String field, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.hincrBy(SafeEncoder.encode(key), SafeEncoder.encode(field), amount);
			}
		});
	}

	@Override
    public long hincr(final String key, final Map<String, Integer> nums){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				Pipeline pipe = conn.pipelined();
                byte[] bk = SafeEncoder.encode(key);
				for(String field : nums.keySet()){
					pipe.hincrBy(bk, SafeEncoder.encode(field), nums.get(field));
				}
				pipe.exec();
				return 1L;
			}
		});
	}

	@Override
    public long hdecr(final String key, final String field, final Integer amount){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.hincrBy(SafeEncoder.encode(key), SafeEncoder.encode(field), -1 * amount);
			}
		});
	}

	@Override
    public long hdecr(final String key, final Map<String, Integer> nums){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hincrBy(bk, SafeEncoder.encode(field), -1 * nums.get(field));
				}
				pipe.exec();
				return 1L;
			}
		});
	}

	@Override
    public Map<String, Integer> hall(final String key){
		return this.execute(new RedisCommand<Map<String, Integer>>(){
			public Map<String, Integer> execute(final Jedis conn) throws Exception {
                Map<byte[], byte[]> bs = conn.hgetAll(SafeEncoder.encode(key));
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
		});
	}

	@Override
    public boolean hrem(final String key, final String... fields){
		return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				return conn.hdel(bk, SafeEncoder.encodeMany(fields)) > 0;
			}
		});
	}

	@Override
    public long hset(final String key, final Map<String, Integer> nums){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				Pipeline pipe = conn.pipelined();
				for(String field : nums.keySet()){
					pipe.hset(bk, SafeEncoder.encode(field), SafeEncoder.encode(String.valueOf(nums.get(field))));
				}
				pipe.exec();
				return 1L;
			}
		});
	}
	

	@Override
    public Long rpush(final String key, final String... values){
		return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.rpush(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
	}

    @Override
    public <T> Long rpush(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = getRedisBuffer().write(values[i]);
                }
                return conn.rpush(SafeEncoder.encode(key), bytes);
            }
        });
    }


    @Override
    public Long lpush(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
                return conn.lpush(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
    }

    @Override
    public <T> Long lpush(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = getRedisBuffer().write(values[i]);
                }
                return conn.lpush(SafeEncoder.encode(key), bytes);
            }
        });
    }

    @Override
    public Long llen(final String key){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.llen(SafeEncoder.encode(key));
			}
		});
    }


    @Override
    public List<String> lrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				List<byte[]> ls = conn.lrange(SafeEncoder.encode(key), start, end);
                List<String> ret = new ArrayList<String>();
                for (byte[] b : ls){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }

    @Override
    public <T> List<T> lrange(final Class<T> clazz, final String key, final int page, final int limit){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final Jedis conn) throws Exception {
                long start = (page - 1) * limit;
                long end = start + limit;
                List<byte[]> ls = conn.lrange(SafeEncoder.encode(key), start, end);
                List<T> ret = Collections.EMPTY_LIST;
                try {
                    ret = getRedisBuffer().read(ls, clazz);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                return ret;
            }
        });
    }


    @Override
    public String ltrim(final String key, final int start, final int end){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.ltrim(SafeEncoder.encode(key), start, end);
			}
		});
    }


    @Override
    public String lindex(final String key, final int index){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				byte[] bs = conn.lindex(SafeEncoder.encode(key), index);
                if (bs == null){
                    return null;
                }
                return new String(bs);
			}
		});
    }

    @Override
    public <T> T lindex(final Class<T> clazz, final String key, final int index){
        return this.execute(new RedisCommand<T>(){
            public T execute(final Jedis conn) throws Exception {
                byte[] bs = conn.lindex(SafeEncoder.encode(key), index);
                if (bs == null){
                    return null;
                }
                return getRedisBuffer().read(bs, clazz);
            }
        });
    }


    @Override
    public String lset(final String key, final int index, final String value){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				return conn.lset(SafeEncoder.encode(key), index, SafeEncoder.encode(value));
			}
		});
    }

    @Override
    public <T> String lset(final Class<T> clazz, final String key, final int index, final T value){
        return this.execute(new RedisCommand<String>(){
            public String execute(final Jedis conn) throws Exception {
                byte[] bytes = getRedisBuffer().write(value);
                return conn.lset(SafeEncoder.encode(key), index, bytes);
            }
        });
    }


    @Override
    public Long lrem(final String key, final String value){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.lrem(SafeEncoder.encode(key), 0, SafeEncoder.encode(value));
			}
		});
    }

    @Override
    public <T> Long lrem(final Class<T> clazz, final String key, final T value){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                byte[] bytes = getRedisBuffer().write(value);
                return conn.lrem(SafeEncoder.encode(key), 0, bytes);
            }
        });
    }


    @Override
    public String lpop(final String key){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				byte[] bs = conn.lpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return SafeEncoder.encode(bs);
			}
		});
    }
    @Override
    public <T> T lpop(final Class<T> clazz, final String key){
        return this.execute(new RedisCommand<T>(){
            public T execute(final Jedis conn) throws Exception {
                byte[] bs = conn.lpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return getRedisBuffer().read(bs, clazz);
            }
        });
    }

    @Override
    public List<String> lpop(final String key, final int limit){
        return this.execute(new RedisCommand<List<String>>(){
            public List<String> execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<String> resp = new ArrayList<String>();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.lpop(bk);
                    if (bs != null){
                        resp.add(SafeEncoder.encode(bs));
                    }

                }
                return resp;
            }
        });
    }

    @Override
    public <T> List<T> lpop(final Class<T> clazz, final String key, final int limit){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<T> resp = new ArrayList<T>();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.lpop(bk);
                    if (bs != null){
                        resp.add(getRedisBuffer().read(bs, clazz));
                    }

                }
                return resp;
            }
        });
    }


    @Override
    public String rpop(final String key){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				byte[] bs = conn.rpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return SafeEncoder.encode(bs);
			}
		});
    }

    @Override
    public <T> T rpop(final Class<T> clazz, final String key){
        return this.execute(new RedisCommand<T>(){
            public T execute(final Jedis conn) throws Exception {
                byte[] bs = conn.rpop(SafeEncoder.encode(key));
                if (bs == null){
                    return null;
                }
                return getRedisBuffer().read(bs, clazz);
            }
        });
    }

    @Override
    public List<String> rpop(final String key, final int limit){
        return this.execute(new RedisCommand<List<String>>(){
            public List<String> execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<String> resp = new ArrayList<String>();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.rpop(bk);
                    if (bs != null){
                        resp.add(SafeEncoder.encode(bs));
                    }

                }
                return resp;
            }
        });
    }

    @Override
    public <T> List<T> rpop(final Class<T> clazz, final String key, final int limit){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
                List<T> resp = new ArrayList<T>();
                for (int i = 0; i < limit; i++) {
                    byte[] bs = conn.rpop(bk);
                    if (bs != null){
                        resp.add(getRedisBuffer().read(bs, clazz));
                    }

                }
                return resp;
            }
        });
    }


    @Override
    public List<String> lsort(final String key){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				List<byte[]> lbs = conn.sort(SafeEncoder.encode(key));
                List<String> ret = new ArrayList<String>();
                for (byte[] b : lbs){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }
    
    @Override
    public Long lpushx(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.lpushx(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
    }

    @Override
    public <T> Long lpushx(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = getRedisBuffer().write(values[i]);
                }
                return conn.lpushx(SafeEncoder.encode(key), bytes);
            }
        });
    }

    @Override
    public Long rpushx(final String key, final String... values){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.rpushx(SafeEncoder.encode(key), SafeEncoder.encodeMany(values));
			}
		});
    }

    @Override
    public <T> Long rpushx(final Class<T> clazz, final String key, final T... values){
        return this.execute(new RedisCommand<Long>(){
            public Long execute(final Jedis conn) throws Exception {
                byte[][] bytes = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = getRedisBuffer().write(values[i]);
                }
                return conn.rpushx(SafeEncoder.encode(key), bytes);
            }
        });
    }

    @Override
    public List<String> blpop(final int timeout, final String... keys){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				List<byte[]> bs = conn.blpop(timeout, SafeEncoder.encodeMany(keys));
                return fromBytes(bs);
			}
		});
    }

    @Override
    public <T> List<T> blpop(final Class<T> clazz, final int timeout, final String... keys){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final Jedis conn) throws Exception {
                List<byte[]> bs = conn.blpop(timeout, SafeEncoder.encodeMany(keys));
                List<T> ret = new ArrayList<T>();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(getRedisBuffer().read(b, clazz));
                    }
                }
                return ret;
            }
        });
    }

    @Override
    public List<String> brpop(final int timeout, final String... keys){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				List<byte[]> bs = conn.brpop(timeout, SafeEncoder.encodeMany(keys));
                return fromBytes(bs);
			}
		});
    }

    @Override
    public <T> List<T> brpop(final Class<T> clazz, final int timeout, final String... keys){
        return this.execute(new RedisCommand<List<T>>(){
            public List<T> execute(final Jedis conn) throws Exception {
                List<byte[]> bs = conn.brpop(timeout, SafeEncoder.encodeMany(keys));
                List<T> ret = new ArrayList<T>();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(getRedisBuffer().read(b, clazz));
                    }
                }
                return ret;
            }
        });
    }


    @Override
    public Long sadd(final String key, final String... members){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.sadd(SafeEncoder.encode(key), SafeEncoder.encodeMany(members));
			}
		});
    }
    

    @Override
    public Long sadd(final String key, final List<?> members){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
                byte[] bk = SafeEncoder.encode(key);
				Pipeline pipe = conn.pipelined();
				for(Object v : members){
					pipe.sadd(bk, SafeEncoder.encode(String.valueOf(v)));
				}
				pipe.exec();
				return 1L;
			}
		});
    }
    

    @Override
    public Set<String> smembers(final String key){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				Set<byte[]> sk = conn.smembers(SafeEncoder.encode(key));
                Set<String> ret = new HashSet<String>();
                for (byte[] b : sk){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
            }
		});
    }


    @Override
    public Long srem(final String key, final String... members){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.srem(SafeEncoder.encode(key), SafeEncoder.encodeMany(members));
			}
		});
    }


    @Override
    public String spop(final String key){
    	return this.execute(new RedisCommand<String>(){
			public String execute(final Jedis conn) throws Exception {
				byte[] bytes = conn.spop(SafeEncoder.encode(key));
                if (bytes == null){
                    return null;
                }
                return SafeEncoder.encode(bytes);
			}
		});
    }


    @Override
    public Long scard(final String key){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.scard(SafeEncoder.encode(key));
			}
		});
    }


    @Override
    public Boolean sismember(final String key, final String member){
    	return this.execute(new RedisCommand<Boolean>(){
			public Boolean execute(final Jedis conn) throws Exception {
				return conn.sismember(SafeEncoder.encode(key), SafeEncoder.encode(member));
			}
		});
    }


    @Override
    public List<String> srandmember(final String key, final int count){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				List<byte[]> lbs = conn.srandmember(SafeEncoder.encode(key), count);
                if (lbs == null){
                    return Collections.emptyList();
                }
                return fromBytes(lbs);
			}
		});
    }
    

    @Override
    public List<String> ssort(final String key){
    	return this.execute(new RedisCommand<List<String>>(){
			public List<String> execute(final Jedis conn) throws Exception {
				List<byte[]> lbs = conn.sort(SafeEncoder.encode(key));
                return fromBytes(lbs);
			}
		});
    }
    

    @Override
    public Long zadd(final String key, final double score, final String member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zadd(SafeEncoder.encode(key), score, SafeEncoder.encode(member));
			}
		});
    }
    

    @Override
    public Long zadd(final String key, final Map<String, Double> scoreMembers){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
                Map<byte[], Double> ms = new HashMap<byte[], Double>();
                Iterator<String> itor = scoreMembers.keySet().iterator();
                while (itor.hasNext()){
                    String k = itor.next();
                    ms.put(SafeEncoder.encode(k), scoreMembers.get(k));
                }
				return conn.zadd(SafeEncoder.encode(key), ms);
			}
		});
    }

    @Override
    public Set<String> zrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				Set<byte[]> bs = conn.zrange(SafeEncoder.encode(key), start, end);
                Set<String> ret = new HashSet<String>();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }
    @Override
    public Set<String> zrevrange(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				Set<byte[]> bs = conn.zrevrange(SafeEncoder.encode(key), start, end);
                Set<String> ret = new HashSet<String>();
                for (byte[] b : bs){
                    if (b != null) {
                        ret.add(SafeEncoder.encode(b));
                    }
                }
                return ret;
			}
		});
    }
    
    @Override
    public Long zrem(final String key, final String... member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zrem(SafeEncoder.encode(key), SafeEncoder.encodeMany(member));
			}
		});
    }

    @Override
    public Double zincrby(final String key, final double score, final String member){
    	return this.execute(new RedisCommand<Double>(){
			public Double execute(final Jedis conn) throws Exception {
				return conn.zincrby(SafeEncoder.encode(key), score, SafeEncoder.encode(member));
			}
		});
    }


    @Override
    public Long zrank(final String key, final String member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zrank(SafeEncoder.encode(key), SafeEncoder.encode(member));
			}
		});
    }


    @Override
    public Long zrevrank(final String key, final String member){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zrevrank(SafeEncoder.encode(key), SafeEncoder.encode(member));
			}
		});
    }
    

    @Override
    public Set<Tuple> zrangeWithScores(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				Set<Tuple> bs = conn.zrangeWithScores(SafeEncoder.encode(key), start, end);
                return bs;
			}
		});
    }
    

    @Override
    public Set<Tuple> zrevrangeWithScores(final String key, final int page, final int limit){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				long start = (page - 1) * limit;
				long end = start + limit;
				return conn.zrevrangeWithScores(SafeEncoder.encode(key), start, end);
			}
		});
    }
    

    @Override
    public Long zcard(final String key){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zcard(SafeEncoder.encode(key));
			}
		});
    }


    @Override
    public Double zscore(final String key, final String member){
    	return this.execute(new RedisCommand<Double>(){
			public Double execute(final Jedis conn) throws Exception {
				return conn.zscore(SafeEncoder.encode(key), SafeEncoder.encode(member));
			}
		});
    }
    
    @Override
    public Long zcount(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zcount(SafeEncoder.encode(key), min, max);
			}
		});
    }

    @Override
    public Long zcount(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zcount(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
			}
		});
    }


    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				Set<byte[]> bs = conn.zrangeByScore(SafeEncoder.encode(key), min, max);
                return fromBytes(bs);
			}
		});
    }

    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				Set<byte[]> bs = conn.zrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
                return fromBytes(bs);
			}
		});
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
				Set<byte[]> bs = conn.zrevrangeByScore(SafeEncoder.encode(key), min, max);
                return fromBytes(bs);
			}
		});
    }
    
    @Override
    public Set<String> zrevrangeByScore(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<String>>(){
			public Set<String> execute(final Jedis conn) throws Exception {
                Set<byte[]> bs = conn.zrevrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
                return fromBytes(bs);
			}
		});
    }
    

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(SafeEncoder.encode(key), min, max);
			}
		});
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(SafeEncoder.encode(key), min, max);
			}
		});
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrangeByScoreWithScores(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
			}
		});
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min){
    	return this.execute(new RedisCommand<Set<Tuple>>(){
			public Set<Tuple> execute(final Jedis conn) throws Exception {
				return conn.zrevrangeByScoreWithScores(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
			}
		});
    }

    @Override
    public Long zremrangeByRank(final String key, final int start, final int end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zremrangeByRank(SafeEncoder.encode(key), start, end);
			}
		});
    }

    @Override
    public Long zremrangeByScore(final String key, final double start, final double end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zremrangeByScore(SafeEncoder.encode(key), start, end);
			}
		});
    }
    
    @Override
    public Long zremrangeByScore(final String key, final String start, final String end){
    	return this.execute(new RedisCommand<Long>(){
			public Long execute(final Jedis conn) throws Exception {
				return conn.zremrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(start), SafeEncoder.encode(end));
			}
		});
    }

}
