package com.argo.redis;

/**
 * Created by yamingd on 9/9/15.
 */
public class RedisConfigTest {

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

}
