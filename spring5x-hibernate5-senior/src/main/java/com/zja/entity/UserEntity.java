package com.zja.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhengJa
 * @description User 实体类
 * @data 2019/10/29
 */
@Data
@ApiModel("用户信息实体类")
public class UserEntity implements Serializable {

    @ApiModelProperty(value = "默认:mysql自增,oracle序列")
    private Integer id;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("年龄")
    private Integer age;
    @ApiModelProperty("不传值,后台创建时间")
    private Date createTime;
    @ApiModelProperty("不传值,后台创建时间")
    private Date updateTime;
}
