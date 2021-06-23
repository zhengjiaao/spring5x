package com.zja.controller;

import com.zja.entity.Student;
import com.zja.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-12-02 14:09
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/mybatis/plus")
@Api(tags = {"StudentController"}, description = "mybatis-plus简单测试")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /** ******* 新增操作 ******* **/

    @PostMapping("insert/student")
    @ApiOperation(value = "静态插入数据:通用方法,必须传id值且id>0", notes = "插入数据(id不自增或不使用序列，必须传id值且id>0)", httpMethod = "POST")
    public Object insertStudent(@RequestBody Student student) {
        return this.studentService.insertStudent(student);
    }


    /** ******* 查询操作 ******* **/

    @GetMapping("get/byid")
    @ApiOperation(value = "1、按id查询", notes = "查询数据", httpMethod = "GET")
    public Object getStudentById(@RequestParam Integer id) {
        return this.studentService.getStudentById(id);
    }

    @GetMapping("get/one")
    @ApiOperation(value = "2、查询一条数据", notes = "查询数据", httpMethod = "GET")
    public Object getOneStudent(String stuName,Integer stuId) {
        return this.studentService.getOneStudent(stuName,stuId);
    }

    @GetMapping("get/byidlist")
    @ApiOperation(value = "3、多个id查询", notes = "查询数据", httpMethod = "GET")
    public Object getStudentByIdList() {
        return this.studentService.getStudentByIdList();
    }

    @GetMapping("get/bypage")
    @ApiOperation(value = "4、分页查询", notes = "查询数据", httpMethod = "GET")
    public Object getStudentByPage(long current, long size) {
        return this.studentService.getStudentByPage(current,size);
    }

    @GetMapping("get/all")
    @ApiOperation(value = "5、查询所有数据", notes = "查询数据", httpMethod = "GET")
    public Object getAllStudent() {
        return this.studentService.getAllStudent();
    }


    /** ******* 更新操作 ******* **/

    @GetMapping("update/student")
    @ApiOperation(value = "1、更新用户", notes = "更新操作", httpMethod = "GET")
    public Object updateStudent() {
        return this.studentService.updateStudent();
    }


    /** ******* 删除操作 ******* **/

    @DeleteMapping("delete/byid")
    @ApiOperation(value = "1、通过id删除", notes = "删除数据", httpMethod = "DELETE")
    public Object deleteStudentById(@RequestParam Integer stuId) {
        return this.studentService.deleteStudentById(stuId);
    }

    @DeleteMapping("delete/bycolumnmap")
    @ApiOperation(value = "2、通过ColumnMap多字段删除", notes = "删除数据", httpMethod = "DELETE")
    public Object deleteStudentByColumnMap() {
        return this.studentService.deleteStudentByColumnMap();
    }

    @DeleteMapping("delete/byidlist")
    @ApiOperation(value = "3、通过多个ID进行删除", notes = "删除数据", httpMethod = "DELETE")
    public Object deleteStudentByIdList() {
        return this.studentService.deleteStudentByIdList();
    }

    @DeleteMapping("delete/bywrapper")
    @ApiOperation(value = "4、根据条件构造器删除", notes = "删除数据", httpMethod = "DELETE")
    public Object deleteStudentByWrapper(@RequestParam String stuName) {
        return this.studentService.deleteStudentByWrapper(stuName);
    }

}
