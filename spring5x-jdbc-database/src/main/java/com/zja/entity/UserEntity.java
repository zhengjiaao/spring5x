package com.zja.entity;

import java.io.Serializable;

/**
 * 用户实体类(对应数据库中的用户表)
 */
public class UserEntity implements Serializable {
    //用户id
    private int id;
    //用户名称
    private String userName;
    //用户年龄
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
