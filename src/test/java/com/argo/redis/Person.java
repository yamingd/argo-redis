package com.argo.redis;

import org.msgpack.annotation.Message;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/3
 * Time: 22:50
 */
@Message
public class Person {

    public Integer id;
    public String name;
    public Date createAt;

    public Person(Integer id) {
        this.id = id;
    }

    public Person() {
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Person{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", createAt=").append(createAt);
        sb.append('}');
        return sb.toString();
    }
}
