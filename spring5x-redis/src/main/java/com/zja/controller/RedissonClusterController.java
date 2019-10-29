package com.zja.controller;

import com.zja.entity.UserEntity;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhengJa
 * @description Redisson 连接redis 单机配置测试
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/redissoncluster")
public class RedissonClusterController {

    @Autowired
    private RedissonClient redissonClient;

    //存数据
    @GetMapping("set/data")
    public Object Set() {
        // key 存在则更新 不存在则存入
        RBucket<UserEntity> rBucket = redissonClient.getBucket("userentity");
        rBucket.set(new UserEntity("张三","22",new Date()));

        //redissonClient.shutdown();
        return rBucket.get();
    }

    //获取数据
    @GetMapping("get/data")
    public Object Get() {
        RBucket<UserEntity> rBucket = redissonClient.getBucket("userentity");

        //redissonClient.shutdown();
        return rBucket.get();

    }

    //设置过期时间
    @GetMapping("get/overdue")
    public Object SetEx() {
        // key 存在则更新 不存在则存入，并设置过期时间
        RBucket<UserEntity> rBucket = redissonClient.getBucket("userentity");
        rBucket.set(new UserEntity("十秒后过期","22",new Date()), 10, TimeUnit.SECONDS);

        //redissonClient.shutdown();
        return "我在十秒后会消失";

    }

}
