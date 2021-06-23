package com.zja.service;

/**
 * Date: 2019-11-27 14:24
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：一对多
 */
public interface OneToManyService {

    Object save();

    Object getDept(int deptId);
    Object getAllDept();

    Object getEmployee(int empId);
    Object getAllEmployee();

}
