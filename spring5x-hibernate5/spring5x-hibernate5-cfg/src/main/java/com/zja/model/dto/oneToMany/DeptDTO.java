package com.zja.model.dto.oneToMany;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Date: 2019-11-27 14:19
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：关键点是通过部门实体类维护到员工的实体类
 */
@Getter
@Setter
public class DeptDTO {
    private int deptId;// 部门编号
    private String deptName;// 部门名称

    //@JSONField(serialize=false)  //被注解的字段不会被序列化
    //@JsonIgnore  //被注解的字段不会被序列化
    //@JsonBackReference //在序列化时，@JsonBackReference的作用相当于@JsonIgnore
    private Set<EmployeeDTO> employeeDTOSet;// 部门对应多个员工，即一对多的关系
}
