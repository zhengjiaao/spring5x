package com.zja.controller;

import com.zja.service.OneToManyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-11-26 13:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：关联关系之一对多
 */
@RestController
@RequestMapping("rest/onetomany")
@Api(tags = {"OneToManyController"}, description = "关联关系之一对多")
public class OneToManyController {

    @Autowired
    private OneToManyService oneToManyService;

    @PostMapping("save")
    @ApiOperation(value = "1、模拟数据入库", notes = "", httpMethod = "POST")
    public Object saveTeacherAndStudent() {
        return this.oneToManyService.save();
    }

    @GetMapping("getDept")
    @ApiOperation(value = "2、根据deptId获取部门信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getDept(@ApiParam(defaultValue = "1") @RequestParam int deptId) {
        return this.oneToManyService.getDept(deptId);
    }

    @GetMapping("getAllDept")
    @ApiOperation(value = "3、获取所有部门信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getAllDept() {
        return this.oneToManyService.getAllDept();
    }

    @GetMapping("getEmployee")
    @ApiOperation(value = "4、根据empId获取员工信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getEmployee(@ApiParam(defaultValue = "1") @RequestParam int empId) {
        return this.oneToManyService.getEmployee(empId);
    }

    @GetMapping("getAllEmployee")
    @ApiOperation(value = "5、获取所有员工信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getAllEmployee() {
        return this.oneToManyService.getAllEmployee();
    }

}
