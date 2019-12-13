package com.zja.controller;

import com.zja.dao.UserEntityDao;
import com.zja.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Date: 2019-12-11 17:19
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/user")
@Api(tags = {"UserEntityController"},description = "jpa接口测试")
public class UserEntityController {

    @Autowired
    private UserEntityDao userEntityDao;

    @ApiOperation(value = "1、模拟数据入库",notes = "jpa",httpMethod = "GET")
    @GetMapping("get/save/user/all")
    public Object saveDataall(){
        List<UserEntity> userEntities = new ArrayList<>();
        for (int i=1;i<10;i++){
            UserEntity userEntity = new UserEntity();
            userEntity.setId(i+0L);
            userEntity.setUserName("李四"+i);
            userEntity.setAge(14+i);
            userEntity.setCreateTime(new Date());
            userEntity.setUpdateTime(new Date());

            userEntities.add(userEntity);
        }
        List<UserEntity> jpas = this.userEntityDao.saveAll(userEntities);
        return jpas;
    }


    @ApiOperation(value = "查询所有对象-不排序",notes = "jpa",httpMethod = "GET")
    @GetMapping("get/user/all")
    public Object getData(){
        //查询所有对象，不排序
        return this.userEntityDao.findAll();
    }

    @ApiOperation(value = "查询所有对象-排序",notes = "jpa",httpMethod = "GET")
    @GetMapping("get/user/all/sort")
    public Object getDataSort(){
        Sort sort =new Sort(Sort.Direction.DESC,"age");
        //查询所有对象，不排序
        return this.userEntityDao.findAll(sort);
    }

    @ApiOperation(value = "查询所有对象-排序-分页",notes = "jpa",httpMethod = "GET")
    @GetMapping("get/user/all/sort/page")
    public Object getDataSortPage(int page,int size){
        Sort sort =new Sort(Sort.Direction.DESC,"age");
        //page 当前页，size 每页显示多少条数据，sort 排序方式
        Pageable pageable = PageRequest.of(2,3,sort);
        Page<UserEntity> entityPage = userEntityDao.findAll(pageable);
        List<UserEntity> userEntityList = entityPage.getContent();
        System.out.println(userEntityList);
        System.out.println("每页几条数据："+entityPage.getSize());
        System.out.println("当前页："+entityPage.getNumber());
        System.out.println("排序方式："+entityPage.getSort());
        System.out.println("总数据条数："+entityPage.getTotalElements());
        System.out.println("总页数"+entityPage.getTotalPages());
        return entityPage;
    }

    @ApiOperation(value = "保存对象",notes = "jpa",httpMethod = "POST")
    @PostMapping("save/user")
    public Object saveData(@RequestBody UserEntity userEntity){
        return this.userEntityDao.save(userEntity);
    }

    @ApiOperation(value = "根据Id查询对象",notes = "jpa",httpMethod = "GET")
    @GetMapping("get/byid/user")
    public Object getData(Long id){
        return this.userEntityDao.findById(id).get();
    }

    @ApiOperation(value = "根据Id删除",notes = "jpa",httpMethod = "GET")
    @DeleteMapping("delete/byid")
    public Object deleteData(Long id){
        try {
            UserEntity userEntity = this.userEntityDao.findById(id).get();
            if (userEntity !=null){
                this.userEntityDao.deleteById(id);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
