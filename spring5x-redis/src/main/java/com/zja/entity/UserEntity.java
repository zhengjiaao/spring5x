package com.zja.entity;

import java.util.Date;

/**
 * @author ZhengJa
 * @description User 对象
 * @data 2019/10/29
 */
public class UserEntity {
    private String name;
    private String age;
    private Date date;

    public UserEntity() {
    }

    public UserEntity(String name, String age,Date date) {
        this.name = name;
        this.age = age;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", date=" + date +
                '}';
    }
}
