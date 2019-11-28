package com.zja.controller;

import com.zja.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhengJa
 * @description hibernate 搭建测试
 * @data 2019/11/19
 */
@RestController
@RequestMapping("rest/hibernate")
@Api(tags = {"HibernateController"}, description = "HibernateTemplate 常用方法使用")
public class HibernateController {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @PostMapping("saveEntity")
    @ApiOperation(value = "save保存用户", notes = "HibernateTemplate", httpMethod = "POST")
    public Object saveEntity(@RequestBody UserEntity userEntity) {
        return this.userService.saveEntity(userEntity);
    }

    @PostMapping("saveEntity2")
    @ApiOperation(value = "save保存用户", notes = "HibernateTemplate", httpMethod = "POST")
    public Object saveEntity2(@RequestBody UserEntity userEntity) {
        return this.userService.saveEntity2(userEntity);
    }

    @PostMapping("mergeEntity")
    @ApiOperation(value = "merge保存用户", notes = "HibernateTemplate", httpMethod = "POST")
    public Object mergeEntity(@RequestBody UserEntity userEntity) {
        return this.userService.mergeEntity(userEntity);
    }

    @PostMapping("mergeEntity2")
    @ApiOperation(value = "merge保存用户", notes = "HibernateTemplate", httpMethod = "POST")
    public Object mergeEntity2(@RequestBody UserEntity userEntity) {
        return this.userService.mergeEntity2(userEntity);
    }


    @GetMapping("getEntity")
    @ApiOperation(value = "get获取用户信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getEntity(@RequestParam Integer id) {
        return this.userService.getEntity(id);
    }

    @GetMapping("loadEntity")
    @ApiOperation(value = "load获取用户信息-失败", notes = "HibernateTemplate", httpMethod = "GET")
    public Object loadEntity(@RequestParam Integer id) {
        return this.userService.loadEntity(id);
    }

    @GetMapping("getListEntity")
    @ApiOperation(value = "get获取用户信息", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getListEntity() {
        return this.userService.getListEntity();
    }
    @GetMapping("getEntityName")
    @ApiOperation(value = "get根据id获取用户信息-失败", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getEntityName(@RequestParam Integer id) {
        return this.userService.getEntityName(id);
    }

    @GetMapping("getPageByConditions")
    @ApiOperation(value = "分页查询", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getPageByConditions() {
        return this.userService.getPageByConditions();
    }

    @PutMapping("updateEntity")
    @ApiOperation(value = "更新用户", notes = "HibernateTemplate", httpMethod = "PUT")
    public Object updateEntity(@RequestBody UserEntity userEntity) {
        this.userService.updateEntity(userEntity);
        return "成功";
    }

    @PutMapping("saveOrUpdate")
    @ApiOperation(value = "更新或保存用户信息", notes = "HibernateTemplate", httpMethod = "PUT")
    public Object saveOrUpdate(@RequestBody UserEntity userEntity) {
        this.userService.saveOrUpdate(userEntity);
        return "成功";
    }

    @DeleteMapping("deleteByEntity")
    @ApiOperation(value = "根据id删除数据", notes = "HibernateTemplate", httpMethod = "DELETE")
    public Object deleteByEntity(@RequestParam Integer id) {
        this.userService.deleteByEntity(id);
        return "成功";
    }

    @DeleteMapping("deleteAll")
    @ApiOperation(value = "删除所有数据", notes = "HibernateTemplate", httpMethod = "DELETE")
    public Object deleteAll() {
        this.userService.deleteAll();
        return "成功";
    }

    @GetMapping("execute")
    @ApiOperation(value = "execute方法", notes = "HibernateTemplate", httpMethod = "GET")
    public Object execute() {
        return this.userService.execute();
    }

    @PostMapping("executeSaveUserEntity")
    @ApiOperation(value = "execute2方法", notes = "HibernateTemplate", httpMethod = "POST")
    public Object executeSaveUserEntity(@RequestBody UserEntity userEntity) {
        return this.userService.executeSaveUserEntity(userEntity);
    }

    @GetMapping("findByExample")
    @ApiOperation(value = "Example查询所有数据", notes = "HibernateTemplate", httpMethod = "GET")
    public Object findByExample() {
        return this.userService.findByExample();
    }

    @GetMapping("findByExample2")
    @ApiOperation(value = "Example查询所有数据-分页", notes = "HibernateTemplate", httpMethod = "GET")
    public Object findByExample(int firstResult, int maxResults) {
        return this.userService.findByExample(firstResult,maxResults);
    }

    @GetMapping("findByCriteria")
    @ApiOperation(value = "Criteria查询所有数据", notes = "HibernateTemplate", httpMethod = "GET")
    public Object findByCriteria() {
        return this.userService.findByCriteria();
    }

    @GetMapping("findPageByCriteria")
    @ApiOperation(value = "Criteria查询所有数据-分页", notes = "HibernateTemplate", httpMethod = "GET")
    public Object findPageByCriteria(int firstResult, int maxResults) {
        return this.userService.findPageByCriteria(firstResult,maxResults);
    }

    @GetMapping("findByCriteria2")
    @ApiOperation(value = "Criteria根据属性查询数据-分页", notes = "HibernateTemplate", httpMethod = "GET")
    public Object findByCriteria2(String propertyName, String propertyValue, int firstResult, int maxResults) {
        return this.userService.findByCriteria(propertyName,propertyValue,firstResult,maxResults);
    }

    @GetMapping("findByCriteria3")
    @ApiOperation(value = "Criteria名字模糊查询数据并按年龄区间和年龄大小获取", notes = "HibernateTemplate", httpMethod = "GET")
    public Object findByCriteria3(@RequestParam int low,@RequestParam int high) {
        return this.userService.findByCriteria(low,high);
    }











}
