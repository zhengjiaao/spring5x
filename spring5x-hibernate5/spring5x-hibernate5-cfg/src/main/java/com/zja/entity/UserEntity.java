package com.zja.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ZhengJa
 * @description User 实体类
 * @data 2019/10/29
 */
@Data
@Entity
@Table(name = "userentity")
@ApiModel("用户信息实体类")
public class UserEntity{

    @ApiModelProperty(value = "默认:mysql自增,oracle序列")
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @ApiModelProperty("用户名")
    @Basic(fetch =FetchType.EAGER,optional = false) //急加载，属性是否允许为null
    @Column(name = "username", nullable = false, length = 225)
    private String userName;
    @ApiModelProperty("年龄")
    @Column(name = "age", nullable = true)
    private Integer age;
    @ApiModelProperty("不传值,后台创建时间")
    @Column(name = "createtime", nullable = true)
    private Date createTime;
    @ApiModelProperty("不传值,后台创建时间")
    @Column(name = "updatetime", nullable = true)
    private Date updateTime;
}
