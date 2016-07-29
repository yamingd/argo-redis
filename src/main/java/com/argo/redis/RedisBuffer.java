package com.argo.redis;

import java.io.IOException;
import java.util.List;

/**
 * 对象序列化
 * Created by dengyaming on 7/29/16.
 */
public interface RedisBuffer {


    /**
     * 将二进制序列化成对象
     *
     * @param bytes 字节流
     * @param c 目标类型
     * @param <T> 目标类型
     * @return T
     * @throws IOException
     */
    <T> T read(byte[] bytes, Class<T> c) throws IOException;

    /**
     * 将二进制序列化成对象
     *
     * @param bytes 字节流
     * @param c 目标类型
     * @param <T> 目标类型
     * @return T
     * @throws IOException
     */
    <T> List<T> read(List<byte[]> bytes, Class<T> c) throws IOException;

    /**
     * 将对象序列成二进制
     * @param v 对象
     * @param <T> 数据类型
     * @return byte[]
     * @throws IOException
     */
    <T> byte[] write(T v) throws IOException;
}
