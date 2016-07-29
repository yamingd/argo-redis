package com.argo.redis;

import org.msgpack.annotation.MessagePackMessage;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/3
 * Time: 22:50
 */
@MessagePackMessage
public class Person implements java.io.Serializable {

    private Integer id;
    private String name;
    private Date createAt;
    private Integer gender;
    private Integer status;
    private Integer abc;

    public Person(Integer id) {
        this.id = id;
    }

    public Person() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAbc() {
        return abc;
    }

    public void setAbc(Integer abc) {
        this.abc = abc;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Person{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", createAt=").append(createAt);
        sb.append(", gender=").append(gender);
        sb.append('}');
        return sb.toString();
    }
}
