package com.zja.controller;

import com.zja.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对用户的增删改查操作
 */
@RestController
@Api(tags = {"UserController"},description = "用户增删改查操作")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("getAllUser")
    @ApiOperation(value = "获取所有用户信息-方式一",httpMethod = "GET")
    public Object getAllUser(){
        return this.userService.getAllUser();
    }

    @GetMapping("findUserByName")
    @ApiOperation(value = "根据名称获取用户信息-方式一",httpMethod = "GET")
    public Object findUserByName(@RequestParam String username){
        return this.userService.findUserByName(username);
    }

    @GetMapping("getAllUser2")
    @ApiOperation(value = "获取所有用户信息-方式二",httpMethod = "GET")
    public Object getAllUser2(){
        return this.userService.getAllUser2();
    }

    @GetMapping("findUserByName2")
    @ApiOperation(value = "根据名称获取用户信息-方式二",httpMethod = "GET")
    public Object findUserByName2(@RequestParam String username){
        return this.userService.findUserByName2(username);
    }

    @GetMapping("saveUser")
    @ApiOperation(value = "新增用户",httpMethod = "GET")
    public Object saveUser(@RequestParam String username,@RequestParam int age){
        return this.userService.saveUser(username,age);
    }

    @GetMapping("updateUser")
    @ApiOperation(value = "更新用户信息",httpMethod = "GET")
    public Object updateUser(@RequestParam String username,@RequestParam int age){
        return this.userService.updateUser(username,age);
    }

    @GetMapping("deleteUserByName")
    @ApiOperation(value = "根据用户名称删除用户信息",httpMethod = "GET")
    public Object deleteUserByName(@RequestParam String username){
        return this.userService.deleteUserByName(username);
    }

    @GetMapping("deleteAllUser")
    @ApiOperation(value = "删除所有用户",httpMethod = "GET")
    public Object deleteAllUser(){
        return this.userService.deleteAllUser();
    }

}
