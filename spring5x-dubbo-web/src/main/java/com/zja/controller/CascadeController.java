package com.zja.controller;

import com.zja.service.CascadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description 订单
 * @data 2019/11/18
 */
@RestController
@RequestMapping("rest/orders")
@Api(tags = {"CascadeController"}, description = "关联查询")
public class CascadeController {

    @Autowired
    private CascadeService cascadeService;

    @GetMapping("getOrderAndUserByOrdersId")
    @ApiOperation(value = "根据订单id 查询订单关联的用户信息", notes = "一对一查询", httpMethod = "GET")
    public Object getOrderAndUserByOrdersId(Integer ordersId) {
        return this.cascadeService.getOrderAndUserByOrdersId(ordersId);
    }

    @GetMapping("getUserAndOrdersByUserId")
    @ApiOperation(value = "根据用户id 查询关联的所有订单信息", notes = "一对多查询", httpMethod = "GET")
    public Object getUserAndOrdersByUserId(Integer userId) {
        return this.cascadeService.getUserAndOrdersByUserId(userId);
    }

    @GetMapping("getUsersAndGroupByUserId")
    @ApiOperation(value = "根据用户id查询用户所属的全部组", notes = "多对多查询,方式一", httpMethod = "GET")
    public Object getUsersAndGroupByUserId(Integer userId) {
        return this.cascadeService.getUsersAndGroupByUserId(userId);
    }

    @GetMapping("getGroupAndUsersByGroupId")
    @ApiOperation(value = "根据用户组id查询组下的所有用户", notes = "多对多查询,方式一", httpMethod = "GET")
    public Object getGroupAndUsersByGroupId(Integer groupId) {
        return this.cascadeService.getGroupAndUsersByGroupId(groupId);
    }

    @GetMapping("getGroup")
    @ApiOperation(value = "查询用户信息(包含所属的全部组)", notes = "多对多查询,方式二", httpMethod = "GET")
    public Object getGroup(Integer groupId) {
        return this.cascadeService.getGroup(groupId);
    }

    @GetMapping("getUsers")
    @ApiOperation(value = "查询用户组信息(包含组下面的全部用户)", notes = "多对多查询,方式二", httpMethod = "GET")
    public Object getUsers(Integer userId) {
        return this.cascadeService.getUsers(userId);
    }

}
