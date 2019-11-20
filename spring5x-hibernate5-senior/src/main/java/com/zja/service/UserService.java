package com.zja.service;

import com.zja.entity.UserEntity;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/19
 */
public interface UserService {

    Object getuser(Integer id);

    Object saveUser(UserEntity userEntity);
}
