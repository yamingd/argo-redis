package com.argo.redis;

import com.argo.redis.impl.KryoPack;
import com.argo.redis.impl.RedisMsgPack;
import org.junit.Test;

import java.util.Date;

/**
 * Created by yamingd on 9/9/15.
 */
public class RedisBucketTest {

    @org.junit.Test
    public void testLoad() throws Exception {
        RedisConfig.load("redis-test2.yaml");
        System.out.println(RedisConfig.instance.toString());
    }

    @org.junit.Test
    public void testDelete() throws Exception {

//        RedisSimpleBuket redisBuket = new RedisSimpleBuket();
//        String[] keys = new String[2];
//        keys[0] = "person:1";
//        keys[1] = "person:2";
//
//        redisBuket.set(keys[0], new Person(1));
//        redisBuket.set(keys[1], new Person(2));
//
//        Person person = redisBuket.get(Person.class, keys[0]);
//        System.out.println(person);
//
//        person = redisBuket.get(Person.class, keys[1]);
//        System.out.println(person);
//
//        redisBuket.delete(keys);
//
//        person = redisBuket.get(Person.class, keys[0]);
//        System.out.println(person);
//
//        person = redisBuket.get(Person.class, keys[1]);
//        System.out.println(person);
    }

    @Test
    public void test_kryo_pack() throws Exception {
        KryoPack pack = new KryoPack();
        Person person = new Person(10);
        person.setName("person10");
        person.setCreateAt(new Date());
        System.out.println(person);
        byte[] bytes = pack.write(person);
        System.out.println(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i]);
            System.out.print(",");
        }
        System.out.println("");
        Person newOne = pack.read(bytes, Person.class);
        System.out.println(newOne);
    }

    @Test
    public void test_kryo_perf_01() throws Exception {
        KryoPack pack = new KryoPack();
        Person person = new Person(10);
        person.setName("person10");
        person.setCreateAt(new Date());
        System.out.println(person);

        long ts = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            byte[] bytes = pack.write(person);
            //System.out.println(bytes.length);
        }
        long ts0 = System.currentTimeMillis() - ts;
        System.out.println("ts0: " + ts0 + ", tps: " + 1000 * 10000 / ts0);
    }

    @Test
    public void test_kryo_pack_int() throws Exception {
        KryoPack pack = new KryoPack();
        byte[] bytes = pack.write(100);
        System.out.println(bytes.length);
        Integer newOne = pack.read(bytes, Integer.class);
        System.out.println(newOne);
    }

    @Test
    public void test_kryo_pack_bytes() throws Exception {
        KryoPack pack = new KryoPack();
        byte[] bytes = new byte[]{1,0,99,111,109,46,97,114,103,111,46,114,101,100,105,115,46,80,101,114,115,111,-18,1,41,1,-95,-115,-109,-56,-29,42,0,1,20,1,112,101,114,115,111,110,49,-80};
        Person newOne = pack.read(bytes, Person.class);
        System.out.println(newOne);
    }

    @Test
    public void test_msg_pack() throws Exception {
        RedisMsgPack pack = new RedisMsgPack();
        Person person = new Person(10);
        person.setName("person10");
        person.setCreateAt(new Date());
        System.out.println(person);
        byte[] bytes = pack.write(person);
        System.out.println(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i]);
            System.out.print(",");
        }
        System.out.println("");
        Person newOne = pack.read(bytes, Person.class);
        System.out.println(newOne);
    }

    @Test
    public void test_msg_perf_01() throws Exception {
        RedisMsgPack pack = new RedisMsgPack();
        Person person = new Person(10);
        person.setName("person10");
        person.setCreateAt(new Date());
        System.out.println(person);

        long ts = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            byte[] bytes = pack.write(person);
            //System.out.println(bytes.length);
        }
        long ts0 = System.currentTimeMillis() - ts;
        System.out.println("ts0: " + ts0 + ", tps: " + 1000 * 10000 / ts0);
    }

    @Test
    public void test_msg_pack_int() throws Exception {
        RedisMsgPack pack = new RedisMsgPack();
        byte[] bytes = pack.write(100);
        System.out.println(bytes.length);
        Integer newOne = pack.read(bytes, Integer.class);
        System.out.println(newOne);
    }

    @Test
    public void test_msg_pack_bytes() throws Exception {
        RedisMsgPack pack = new RedisMsgPack();
        byte[] bytes = new byte[]{-108,10,-88,112,101,114,115,111,110,49,48,-49,0,0,1,86,57,4,72,107,-64};
        Person newOne = pack.read(bytes, Person.class);
        System.out.println(newOne);
    }
}
