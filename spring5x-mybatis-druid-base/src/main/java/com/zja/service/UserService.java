package com.zja.service;

import com.zja.entity.UserEntity;

import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/14
 */
public interface UserService {

    List<UserEntity> queryUserById(Integer id);
}
