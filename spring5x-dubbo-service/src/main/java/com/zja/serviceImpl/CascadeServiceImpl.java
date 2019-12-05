package com.zja.serviceImpl;

import com.zja.dao.CascadeDao;
import com.zja.model.dto.GroupEntityDTO;
import com.zja.model.dto.OrdersEntityDTO;
import com.zja.model.dto.UserEntityDTO;
import com.zja.model.entity.GroupEntity;
import com.zja.model.entity.OrdersEntity;
import com.zja.model.entity.UserEntity;
import com.zja.service.CascadeService;
import org.dozer.Mapper;
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

    @Autowired
    private Mapper mapper;

    /**
     * 根据订单id查询用户信息(一对一)，一个订单对应一个用户
     * @param ordersId 订单id
     */
    @Override
    public OrdersEntityDTO getOrderAndUserByOrdersId(Integer ordersId) {
        OrdersEntity ordersEntity = ordersDao.getOrderAndUserByOrdersId(ordersId);
        return mapper.map(ordersEntity, OrdersEntityDTO.class);
    }

    /**
     * 根据用户id查询所有订单信息(一对多)，一个用户对应多个订单
     * @param userId 用户id
     */
    @Override
    public UserEntityDTO getUserAndOrdersByUserId(Integer userId) {
        UserEntity userEntity = ordersDao.getUserAndOrdersByUserId(userId);
        return mapper.map(userEntity,UserEntityDTO.class);
    }

    /**
     * 根据用户id查询用户所属组(全部)(多对多)
     * @param userId 用户id
     */
    @Override
    public UserEntityDTO getUsersAndGroupByUserId(Integer userId) {
        UserEntity userEntity = ordersDao.getUsersAndGroupByUserId(userId);
        return mapper.map(userEntity,UserEntityDTO.class);
    }

    /**
     * 根据用户组id查询组下的所有用户(多对多)
     * @param groupId 组id
     */
    @Override
    public GroupEntityDTO getGroupAndUsersByGroupId(Integer groupId) {
        GroupEntity groupEntity = ordersDao.getGroupAndUsersByGroupId(groupId);
        return mapper.map(groupEntity,GroupEntityDTO.class);
    }

    /**
     * 查询用户信息(包含所属的全部组)
     * @param userId
     */
    @Override
    public UserEntityDTO getUsers(Integer userId) {
        UserEntity userEntity = ordersDao.getUsers(userId);
        return mapper.map(userEntity,UserEntityDTO.class);
    }

    /**
     * 查询用户组信息(包含组下面的全部用户)
     * @param groupId
     */
    @Override
    public GroupEntityDTO getGroup(Integer groupId) {
        GroupEntity groupEntity = ordersDao.getGroup(groupId);
        return mapper.map(groupEntity,GroupEntityDTO.class);
    }

}
