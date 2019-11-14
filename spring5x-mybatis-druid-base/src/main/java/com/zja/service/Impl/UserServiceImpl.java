package com.zja.service.Impl;

import com.zja.dao.UserDao;
import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    //@Transactional(propagation= Propagation.REQUIRES_NEW)
    @Override
    public List<UserEntity> queryUserById(Integer id) {
        return userDao.queryUserById(id);
    }
}
