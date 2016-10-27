package com.argo.redis;

import com.argo.redis.impl.RedisMsgPack;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yamingd on 9/9/15.
 */
public class RedisClusterTest {

    private RedisClusterBuket redisClusterBuket;

    @Before
    public void initialize() throws Exception {
        RedisConfig.load("redis.yaml");
        System.out.println(RedisConfig.instance.toString());
        redisClusterBuket = new RedisClusterBuket();
    }

    @Test
    public void test_set_get(){
        ExecutorService executor = Executors.newCachedThreadPool();
        Collection<Callable<Boolean>> tasks = new ArrayList<>();
        long ts = System.currentTimeMillis();
        int count = 3000;

        for (int i=0; i<count; i++) {

            tasks.add(new Callable<Boolean>() {

                @Override
                public Boolean call() {

                    Random random = new Random(new Date().getTime());
                    int id = random.nextInt(10000);
                    String key = String.format("person:{%s}:profile", id);
                    // System.out.println("key: " + key);
                    Person person = new Person(id);
                    person.setCreateAt(new Date());
                    person.setGender(1);

                    redisClusterBuket.set(key, person);

                    Person cached = redisClusterBuket.get(Person.class, key);

                    // System.out.println("Cached: " + cached);
                    return cached != null;
                }
            });

        }

        try {
            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ts = System.currentTimeMillis() - ts;
        System.out.println("TS: " + (count * 1000 / ts));
    }

    @Test
    public void test_get(){
        Random random = new Random(new Date().getTime());

        for (int i=0; i<10000; i++) {
            String key = String.format("person:{%s}:profile", random.nextInt(10000));
            Person cached = redisClusterBuket.get(Person.class, key);
            System.out.println("Cached: " + cached);
        }
    }

    @Test
    public void test_size(){
        Random random = new Random(new Date().getTime());
        int id = random.nextInt(10000);
        Person person = new Person(id);
        person.setCreateAt(new Date());
        person.setGender(1);
        RedisMsgPack redisBuffer = new RedisMsgPack();
        try {

            byte[] bytes = redisBuffer.write(person);
            System.out.println(bytes.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
