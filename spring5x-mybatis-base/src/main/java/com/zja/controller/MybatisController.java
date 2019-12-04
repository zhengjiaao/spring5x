package com.zja.controller;

import com.github.pagehelper.PageInfo;
import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ZhengJa
 * @description MybatisController 测试类
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/mybatis")
@Api(tags = {"MybatisController"}, description = "mybatis简单测试")
public class MybatisController {

    @Autowired
    private UserService userService;

    @PostMapping("insertUser")
    @ApiOperation(value = "静态插入数据:通用方法,必须传id值且id>0", notes = "插入数据(id不自增或不使用序列，必须传id值且id>0)", httpMethod = "POST")
    public int insertUser(@RequestBody UserEntity userEntity) {
        return this.userService.insertUser(userEntity);
    }

    @PostMapping("insertUserMysql")
    @ApiOperation(value = "动态插入数据: mysql用法 id自增,不传id值", notes = "插入数据(id自增，不传id值)", httpMethod = "POST")
    public int insertUserMysql(@RequestParam String userName,@RequestParam Integer age) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setAge(age);
        userEntity.setCreateTime(new Date());
        return this.userService.insertUserMysql(userEntity);
    }

    @PostMapping("insertUserOracle")
    @ApiOperation(value = "动态插入数据:oracle用法 id使用序列,不传id值", notes = "插入数据(id使用序列，不传id值)", httpMethod = "POST")
    public int insertUserOracle(@RequestParam String userName,@RequestParam Integer age) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setAge(age);
        userEntity.setCreateTime(new Date());
        return this.userService.insertUserOracle(userEntity);
    }

    @PostMapping("mysqlBatchSaveUser")
    @ApiOperation(value = "mybatis+mysql批量插入数据: mysql用法 id自增", notes = "插入数据(id自增)", httpMethod = "POST")
    public int mysqlBatchSaveUser(@ApiParam(value = "count 批量插入几条",defaultValue = "5") @RequestParam Integer count) {

        List<UserEntity> entityList = new ArrayList<>();
        for (int i=0;i<count;i++){
            UserEntity userEntity = new UserEntity();
            userEntity.setUserName("Zhengja_"+i);
            userEntity.setAge(20+i);
            userEntity.setCreateTime(new Date());
            entityList.add(userEntity);
        }
        return this.userService.mysqlBatchSaveUser(entityList);
    }

    @PostMapping("oracleBatchSaveUser")
    @ApiOperation(value = "mybatis+oracle批量插入数据: oracle用法 id不使用序列", notes = "插入数据(id不能使用序列)", httpMethod = "POST")
    public int oracleBatchSaveUser(@ApiParam(value = "count 批量插入几条",defaultValue = "5") @RequestParam Integer count) {

        List<UserEntity> entityList = new ArrayList<>();
        for (int i=0;i<count;i++){
            UserEntity userEntity = new UserEntity();
            //批量插入没有提交，无法获取递增的序列值，所以，oracle注意，id不能使用序列，会报异常 “违反唯一约束条件”
            userEntity.setId(100L+i);
            userEntity.setUserName("Zhengja_"+i);
            userEntity.setAge(20+i);
            userEntity.setCreateTime(new Date());
            entityList.add(userEntity);
        }
        return this.userService.oracleBatchSaveUser(entityList);
    }

    @GetMapping("queryUserById")
    @ApiOperation(value = "按id查询用户", notes = "按id查询数据", httpMethod = "GET")
    public UserEntity queryUserById(@RequestParam Long id) {
        return this.userService.queryUserById(id);
    }

    @GetMapping("queryAllUser")
    @ApiOperation(value = "查询所有用户", notes = "查询所有数据", httpMethod = "GET")
    public List<UserEntity> queryAllUser() {
        return this.userService.queryAllUser();
    }

    @GetMapping("queryByUserName")
    @ApiOperation(value = "按用户名称查询", notes = "按用户名称查询", httpMethod = "GET")
    public List<UserEntity> queryByUserName(String userName) {
        return this.userService.queryByUserName(userName);
    }

    @GetMapping("getpage")
    @ApiOperation(value = "获取分页结果", notes = "分页查询", httpMethod = "GET")
    public List<UserEntity> getPagingResults(@ApiParam("页码值") @RequestParam int pageNum, @ApiParam("每页显示条数") @RequestParam int pageSize) {
        return this.userService.getPagingResults(pageNum, pageSize);
    }

    @GetMapping("getpageinfo")
    @ApiOperation(value = "获取分页结果及分页信息", notes = "分页查询", httpMethod = "GET")
    public PageInfo<UserEntity> queryPageInfo(@ApiParam("页码值") @RequestParam int pageNum, @ApiParam("每页显示条数") @RequestParam int pageSize) {
        return this.userService.queryPageInfo(pageNum, pageSize);
    }

    @PutMapping("updateUser")
    @ApiOperation(value = "更新用户信息", notes = "更新数据-改数据", httpMethod = "PUT")
    public int updateUser(@RequestBody UserEntity userEntity) {
        return this.userService.updateUser(userEntity);
    }

    @DeleteMapping("delUser")
    @ApiOperation(value = "删除数据", notes = "删除数据", httpMethod = "DELETE")
    public int delUser(@RequestParam Long id) {
        return this.userService.delUser(id);
    }

}
