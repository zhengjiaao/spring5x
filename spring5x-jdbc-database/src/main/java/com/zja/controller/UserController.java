package com.zja.controller;

import com.zja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对用户的增删改查操作
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    //获取所有用户信息
    @GetMapping("getAllUser")
    public Object getAllUser(){
        return this.userService.getAllUser();
    }

    //根据名称获取用户信息
    @GetMapping("findUserByName")
    public Object findUserByName(@RequestParam String username){
        return this.userService.findUserByName(username);
    }


    //获取所有用户信息
    @GetMapping("getAllUser2")
    public Object getAllUser2(){
        return this.userService.getAllUser2();
    }

    //根据名称获取用户信息
    @GetMapping("findUserByName2")
    public Object findUserByName2(@RequestParam String username){
        return this.userService.findUserByName2(username);
    }


    //新增用户
    @GetMapping("saveUser")
    public Object saveUser(@RequestParam String username,@RequestParam int age){
        return this.userService.saveUser(username,age);
    }

    //更新用户信息
    @GetMapping("updateUser")
    public Object updateUser(@RequestParam String username,@RequestParam int age){
        return this.userService.updateUser(username,age);
    }

    //根据用户名称删除用户信息
    @GetMapping("deleteUserByName")
    public Object deleteUserByName(@RequestParam String username){
        return this.userService.deleteUserByName(username);
    }

    //删除所有用户
    @GetMapping("deleteAllUser")
    public Object deleteAllUser(){
        return this.userService.deleteAllUser();
    }

}
