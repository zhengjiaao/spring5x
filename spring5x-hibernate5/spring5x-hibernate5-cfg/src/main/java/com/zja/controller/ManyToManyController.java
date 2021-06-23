package com.zja.controller;

import com.zja.service.ManyToManyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-11-26 13:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/manytomany")
@Api(tags = {"ManyToManyController"}, description = "关联关系之多对多")
public class ManyToManyController {

    @Autowired
    private ManyToManyService manyToManyService;

    @PostMapping("saveTeacherAndStudent")
    @ApiOperation(value = "1、模拟数据入库", notes = "一个学生对多个老师，一个老师对多个学生", httpMethod = "POST")
    public Object saveTeacherAndStudent() {
        return this.manyToManyService.saveTeacherAndStudent();
    }

    @GetMapping("getTeacher")
    @ApiOperation(value = "2、根据teaId获取老师信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getTeacher(@ApiParam(defaultValue = "1") @RequestParam int teaId) {
        return this.manyToManyService.getTeacher(teaId);
    }

    @GetMapping("getAllTeacher")
    @ApiOperation(value = "3、获取所有老师信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getAllTeacher() {
        return this.manyToManyService.getAllTeacher();
    }

    @GetMapping("stuId")
    @ApiOperation(value = "4、根据teaId获取学生信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object stuId(@ApiParam(defaultValue = "1") @RequestParam int stuId) {
        return this.manyToManyService.getStudent(stuId);
    }

    @GetMapping("getAllStudent")
    @ApiOperation(value = "5、获取所有学生信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getAllStudent() {
        return this.manyToManyService.getAllStudent();
    }

}
