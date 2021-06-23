package com.zja.entity;

import java.io.Serializable;

/**
 * @author ZhengJa
 * @description User 实体类
 * @data 2019/10/29
 */
public class UserEntity implements Serializable {

    private Integer id;
    private String userName;
    private Integer age;

    public UserEntity(Integer id, String userName, Integer age) {
        this.id = id;
        this.userName = userName;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
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
