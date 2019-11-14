package com.zja.controller;

import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ZhengJa
 * @description MybatisController 测试类
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/mybatis")
public class MybatisController {

    @Autowired
    private UserService userService;

    @GetMapping("get/user/byid")
    public Object queryUserById(@RequestParam Integer id) {
        //注意不可直接get(0)返回，可能获取的值为null
        List<UserEntity> userEntities = userService.queryUserById(id);
        if (userEntities.size() == 0) {
            throw new RuntimeException("查询数据为null");
        }
        return userService.queryUserById(id).get(0);
    }

}
