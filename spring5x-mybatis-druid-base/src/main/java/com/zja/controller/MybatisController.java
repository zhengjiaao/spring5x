package com.zja.controller;

import com.zja.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description MybatisController 测试类
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/mybatis")
public class MybatisController {

    @Autowired
    private UserDao userDao;

    @GetMapping("get/user/byid")
    public Object queryUserById(@RequestParam Integer id){
        return userDao.queryUserById(id).get(0);
    }
}
