package com.zja.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * Date: 2019-12-02 13:10
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Data
@TableName(value = "student") //指定表名
public class Student extends Model<Student> implements Serializable{
    //value与数据库主键列名一致，若实体类属性名与表主键列名一致可省略value
    @TableId(value = "stuid",type = IdType.AUTO) //指定自增策略
    private Long stuId;
    //若没有开启驼峰命名，或者表中列名不符合驼峰规则，可通过该注解指定数据库表中的列名，exist标明数据表中有没有对应列
    @TableField(value = "stuname",exist = true)
    private String stuName;
    //exist是否为数据库表字段（ 默认 true 存在，false 不存在 ）
    //@TableField(exist = true)
    @TableField(value = "stuage")
    private Integer stuAge;
    private String email;
}
