package com.zja.service.Impl;

import com.zja.dao.CascadeDao;
import com.zja.entity.GroupEntity;
import com.zja.entity.OrdersEntity;
import com.zja.entity.UserEntity;
import com.zja.service.CascadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ZhengJa
 * @description 订单具体操作类
 * @data 2019/11/18
 */
@Service
public class CascadeServiceImpl implements CascadeService {

    @Autowired
    private CascadeDao ordersDao;

    /**
     * 根据订单id查询用户信息(一对一)，一个订单对应一个用户
     * @param ordersId 订单id
     */
    @Override
    public OrdersEntity getOrderAndUserByOrdersId(Integer ordersId) {
        return ordersDao.getOrderAndUserByOrdersId(ordersId);
    }

    /**
     * 根据用户id查询所有订单信息(一对多)，一个用户对应多个订单
     * @param userId 用户id
     */
    @Override
    public UserEntity getUserAndOrdersByUserId(Integer userId) {
        return ordersDao.getUserAndOrdersByUserId(userId);
    }

    /**
     * 根据用户id查询用户所属组(全部)(多对多)
     * @param userId 用户id
     */
    @Override
    public UserEntity getUsersAndGroupByUserId(Integer userId) {
        return ordersDao.getUsersAndGroupByUserId(userId);
    }

    /**
     * 根据用户组id查询组下的所有用户(多对多)
     * @param groupId 组id
     */
    @Override
    public GroupEntity getGroupAndUsersByGroupId(Integer groupId) {
        return ordersDao.getGroupAndUsersByGroupId(groupId);
    }

    /**
     * 查询用户信息(包含所属的全部组)
     * @param userId
     */
    @Override
    public UserEntity getUsers(Integer userId) {
        return ordersDao.getUsers(userId);
    }

    /**
     * 查询用户组信息(包含组下面的全部用户)
     * @param groupId
     */
    @Override
    public GroupEntity getGroup(Integer groupId) {
        return ordersDao.getGroup(groupId);
    }
}
