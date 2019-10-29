package com.zja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author ZhengJa
 * @description Jedis 连接 redis 单机配置测试
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/jediscluster")
public class JedisClusterController {

    @Autowired
    private JedisCluster jedisCluster;


    //存数据
    @GetMapping("set/data")
    public Object Set() {
        String value = jedisCluster.set("hello", "我是value");
        return value;
    }

    //获取数据
    @GetMapping("get/data")
    public Object Get() {
        String value = jedisCluster.get("hello");
        return value;
    }

    //设置过期时间
    @GetMapping("get/overdue")
    public Object setEx() {
        String value = jedisCluster.setex("hello", 10, "我会在 10 秒后过期");
        return value;
    }


}
