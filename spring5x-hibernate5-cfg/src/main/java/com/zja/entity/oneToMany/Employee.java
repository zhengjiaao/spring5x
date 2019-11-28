package com.zja.entity.oneToMany;

import lombok.Getter;
import lombok.Setter;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-27 14:20
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Getter
@Setter
public class Employee {
    private int empId;// 员工的编号
    private String empName;// 员工的名称
    private double salary;// 员工的薪资

    //@JSONField(serialize=false)  //被注解的字段不会被序列化
    //@JsonIgnore //被注解的字段不会被序列化
    //@JsonBackReference //尽量放到get方法上，在序列化时，@JsonBackReference的作用相当于@JsonIgnore
    private Dept dept;// 员工和部门的关系
}
