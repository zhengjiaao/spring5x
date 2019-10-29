package com.zja.service.Impl;

import com.zja.entity.AopEntiry;
import com.zja.service.AopService;

/**
 * @author ZhengJa
 * @description aop 测试 的serviceimpl实现类
 * @data 2019/10/28
 */
public class AopServiceImpl implements AopService {

    @Override
    public Object findData(AopEntiry aopEntiry) {

        System.out.println("aopEntiry"+aopEntiry);

        return "  返回值："+aopEntiry.getName();
    }
}
