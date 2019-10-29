package com.zja.entity;

import lombok.Data;

/**
 * @author ZhengJa
 * @description aop 测试实体类
 * @data 2019/10/28
 */
@Data
public class AopEntiry {
    private String name;
    private Integer age;

    @Override
    public String toString() {
        return "AopEntiry{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
