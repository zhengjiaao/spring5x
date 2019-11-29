package com.zja.service;

/**
 * Date: 2019-11-28 15:59
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface UserService {

    Object saveUser(String username,int age);

    Object updateUser(String username,int age);

    Object findUserByName(String username);

    Object getAllUser();

    Object findUserByName2(String username);

    Object getAllUser2();

    Object deleteUserByName(String username);

    Object deleteAllUser();
}
