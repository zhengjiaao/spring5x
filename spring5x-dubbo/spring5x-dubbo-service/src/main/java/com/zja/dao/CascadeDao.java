package com.zja.dao;

import com.zja.model.entity.GroupEntity;
import com.zja.model.entity.OrdersEntity;
import com.zja.model.entity.UserEntity;

import java.util.List;

/**
 * @author ZhengJa
 * @description 订单接口
 * @data 2019/11/18
 */
public interface CascadeDao {

    //根据订单id查询用户信息(一对一)，一个订单对应一个用户
    OrdersEntity getOrderAndUserByOrdersId(Integer ordersId);
    //根据用户id查询所有订单信息(一对多)，一个用户对应多个订单
    UserEntity getUserAndOrdersByUserId(Integer userId);

    /**多对多（方式一）**/
    //根据用户id查询用户所属组(全部)(多对多)
    UserEntity getUsersAndGroupByUserId(Integer userId);
    //根据用户组id查询组下的所有用户(多对多)
    GroupEntity getGroupAndUsersByGroupId(Integer groupId);

    /**多对多（方式二）：将上面方式一的步骤查分两步实现**/
    //根据用户组id 获取组下的全部用户(List<UserEntity>)
    List<UserEntity> getUsersByGroupId(Integer groupId);
    //根据用户id查询用户所属全部组(List<GroupEntity>)
    List<GroupEntity> getGroupByUsersId(Integer userId);

    //查询用户信息(包含所属的全部组)
    UserEntity getUsers(Integer userId);
    //查询用户组信息(包含组下面的全部用户)
    GroupEntity getGroup(Integer groupId);

}
