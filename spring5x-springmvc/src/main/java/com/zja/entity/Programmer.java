package com.zja.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @author ZhengJa
 * @description 测试参数绑定的类
 * @data 2019/10/22
 */
@ApiModel(value = "Programmer")
@Data
public class Programmer implements Serializable {

    private String name;

    //数据校验
    @Min(value = 0,message = "年龄不能为负数！" )
    private int age;

    @Min(value = 0,message = "薪酬不能为负数！" )
    private double salary;

    @ApiModelProperty(value = "默认: 2019-10-22 09:02:02")
    private String birthday;
}
