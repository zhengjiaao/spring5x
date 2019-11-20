package com.zja.service.Impl;

import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/19
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private HibernateTemplate hibernateTemplate;

    @Override
    public Object getuser(Integer id) {
        return hibernateTemplate.get(UserEntity.class,id);
    }

    @Override
    public Object saveUser(UserEntity userEntity){
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.save(userEntity);
    }
}
