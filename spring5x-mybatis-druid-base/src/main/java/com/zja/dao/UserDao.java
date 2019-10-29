package com.zja.dao;

import com.zja.entity.UserEntity;

import java.util.List;

/**
 * @author ZhengJa
 * @description UserDao 接口
 * @data 2019/10/29
 */
public interface UserDao {

    //保存用户
    //UserEntity saveUser(UserEntity userEntity);
    //按id查询用户
    List<UserEntity> queryUserById(Integer id);
    //查询所有用户
    //List<UserEntity> queryAllUser();

}
