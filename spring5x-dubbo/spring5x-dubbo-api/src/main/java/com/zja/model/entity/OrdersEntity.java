package com.zja.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhengJa
 * @description 订单实体类
 * @data 2019/11/18
 */
@Data
@ApiModel("订单实体类")
public class OrdersEntity implements Serializable {
    @ApiModelProperty(value = "默认:mysql自增,oracle序列")
    private Integer ordersId;
    @ApiModelProperty("用户id")
    private Integer userId;
    @ApiModelProperty("订单名称")
    private String ordersName;

    @ApiModelProperty("用户信息")
    private UserEntity userEntity;
}
