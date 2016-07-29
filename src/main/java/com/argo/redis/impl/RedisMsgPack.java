package com.argo.redis.impl;

import com.argo.redis.RedisBuffer;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用MessagePack做序列化
 * Created by dengyaming on 7/29/16.
 */
public class RedisMsgPack implements RedisBuffer {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected MessagePack messagePack = new MessagePack();


    @Override
    public <T> T read(byte[] bytes, Class<T> c) throws IOException {
        if (null == bytes || bytes.length == 0){
            return null;
        }
        try {
            return messagePack.read(bytes, c);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> List<T> read(List<byte[]> bytes, Class<T> c) throws IOException {
        List<T> ret = new ArrayList<T>();
        for (byte[] item : bytes){
            if (item != null) {
                ret.add(read(item, c));
            }else{
                ret.add(null);
            }
        }
        return ret;
    }

    @Override
    public <T> byte[] write(T v) throws IOException {
        if (null == v){
            return null;
        }
        try {
            return messagePack.write(v);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
