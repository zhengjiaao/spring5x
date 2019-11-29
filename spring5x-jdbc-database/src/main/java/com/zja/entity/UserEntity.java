package com.zja.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类(对应数据库中的用户表)
 */
@Data
public class UserEntity implements Serializable {
    //用户id
    private Long id;
    //用户名称
    private String userName;
    //用户年龄
    private int age;
}
