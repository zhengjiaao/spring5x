package com.zja.service;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
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
