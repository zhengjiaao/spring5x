package com.zja.controller;

import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhengJa
 * @description hibernate 搭建测试
 * @data 2019/11/19
 */
@RestController
@RequestMapping("rest/hibernate")
@Api(tags = {"HibernateController"}, description = "Hibernate 基础搭建")
public class HibernateController {

    @Autowired
    private UserService userService;

    @GetMapping("getuser")
    @ApiOperation(value = "获取用户信息", notes = "hibernate", httpMethod = "GET")
    public Object getuser(@RequestParam Integer id) {
        return this.userService.getuser(id);
    }

    @PostMapping("saveUser")
    @ApiOperation(value = "保存用户信息", notes = "hibernate", httpMethod = "POST")
    public Object saveUser(@RequestBody UserEntity userEntity) {
        return this.userService.saveUser(userEntity);
    }
}
