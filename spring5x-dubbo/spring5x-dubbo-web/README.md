# spring5x-dubbo-web
[toc]

spring5x-dubbo-web此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构
如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、spring集成 dubbo依赖和xml配置
- 2、实体类和接口层
- 3、web层调用
- 4、项目的github和博客地址

**项目架构：spring5.x+dubbo**

以下只贴dubbo有关配置和代码，本篇仅将dubbo使用。

### 1、spring集成 dubbo依赖和xml配置
dubboy依赖
```xml
        <!--dubbo 依赖-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.6.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>4.0.0</version>
        </dependency>
        <!--默认是最新的3.5.x版本，而我版本安装zk是3.4.9，所以要用3.4.x版本，不然报异常-->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.13</version>
        </dependency>

```

spring-dubbo-consumer.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="${dubbo.application.name}"/>

    <!--Dubbo 缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题，默认 check="true"。-->
    <!--可以关闭所有服务的启动时检查 -->
    <dubbo:consumer check="false" />

    <!--定义dubbo调用本地注册中心zk服务地址-->
    <dubbo:registry id="local" protocol="zookeeper" address="${dubbo.local.registry.address}"/>
    <!--定义dubbo调用远程注册中心zk服务地址-->
    <!--<dubbo:registry id="remote" protocol="zookeeper" address="${dubbo.remote.registry.address}"/>-->

    <!--定义dubbo所在服务执行时暴露给客户端的端口-->
    <dubbo:protocol name="dubbo" port="${dubbo.protocol.port}"/>


    <!-- 生成远程服务代理，可以和本地 bean 一样使用 Service-->
    <!--本地(local)-->
    <dubbo:reference registry="local" id="cascadeService" interface="com.zja.service.CascadeService" version="${dubbo.interface.version}"/>
    <dubbo:reference registry="local" id="userService" interface="com.zja.service.UserService" version="${dubbo.interface.version}"/>

    <!--本地(local)-->
    <!--<dubbo:reference registry="local" interface="com.zja.service.CascadeService" id="bannerService"
                     version="${dubbo.interface.version}" check="false"/>-->
    <!--远程(remote)-->
    <!--<dubbo:reference registry="remote" id="permissionSupplier" interface="com.dist.dcc.security.auth.api.PermissionSupplier"
                     check="false" version="${dubbo.remote.interface.version}" />-->
</beans>

```

### 2、实体类和接口层
实体类：存在spring5x-dubbo-api模块中
```java
/**
 * @author ZhengJa
 * @description User 实体类
 * @data 2019/10/29
 */
@Data
@ApiModel("用户信息实体类")
public class UserEntity implements Serializable {

    @ApiModelProperty(value = "默认:mysql自增,oracle序列")
    private Integer id;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("年龄")
    private Integer age;
    @ApiModelProperty("不传值,后台创建时间")
    private Date createTime;

    @ApiModelProperty("订单信息")
    private List<OrdersEntity> ordersEntityList;
    @ApiModelProperty("所属组信息")
    private List<GroupEntity> groupEntityList;
}

```
serviceImpl层：存在spring5x-dubbo-service模块中
```java
package com.zja.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zja.dao.UserDao;
import com.zja.model.dto.UserEntityDTO;
import com.zja.model.entity.UserEntity;
import com.zja.service.UserService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private Mapper mapper;

    //静态插入数据:通用方法
    @Override
    public int insertUser(UserEntity userEntity) {
        UserEntity userById = this.userDao.queryUserById(userEntity.getId());
        if (userById !=null){
            throw new RuntimeException("id自增，默认可不传id，是唯一主键，已经存在："+userEntity.getId());
        }
        userEntity.setCreateTime(new Date());
        return this.userDao.insertUser(userEntity);
    }

    //动态插入数据: mysql用法，id自增
    @Override
    public int insertUserMysql(UserEntity userEntity) {
        return this.userDao.insertUserMysql(userEntity);
    }

    //动态插入数据:oracle用法，id使用序列
    @Override
    public int insertUserOracle(UserEntity userEntity) {
        return this.userDao.insertUserOracle(userEntity);
    }

    //mybatis批量插入数据:mysql用法，id自增
    @Override
    public int mysqlBatchSaveUser(List<UserEntity> userEntities) {
        return this.userDao.mysqlBatchSaveUser(userEntities);
    }

    //mybatis批量插入数据:oracle用法，id使用序列
    @Override
    public int oracleBatchSaveUser(List<UserEntity> userEntities) {
        return this.userDao.oracleBatchSaveUser(userEntities);
    }

    //按id查询用户
    @Override
    public UserEntityDTO queryUserById(Integer id) {
        UserEntity userEntity = this.userDao.queryUserById(id);
        return mapper.map(userEntity, UserEntityDTO.class);
    }

    //查询所有用户
    @Override
    public List<UserEntity> queryAllUser() {
        return this.userDao.queryAllUser();
    }

    /**
     * 获取分页结果
     * @param pageNum 页码值
     * @param pageSize 每页显示条数
     */
    @Override
    public List<UserEntity> getPagingResults(int pageNum, int pageSize) {
        //第一个参数是页码值，第二个参数是每页显示条数，第三个参数默认查询总数count
        //获取第pageNum页，每页pageSize条内容，默认查询总数count
        PageHelper.startPage(pageNum, pageSize);
        //紧跟着的第一个select方法会被分页
        return this.userDao.queryAllUser();
    }

    /**
     * 获取分页结果及分页信息
     * @param pageNum 页码值
     * @param pageSize 每页显示条数
     */
    @Override
    public PageInfo<UserEntity> queryPageInfo(int pageNum, int pageSize) {
        //第一个参数是页码值，第二个参数是每页显示条数，第三个参数默认查询总数count
        //获取第pageNum页，每页pageSize条内容，默认查询总数count
        PageHelper.startPage(pageNum, pageSize,true);
        //紧跟着的第一个select方法会被分页
        List<UserEntity> userEntities = this.userDao.queryAllUser();

        //分页信息
        PageInfo<UserEntity> pageInfo = new PageInfo<UserEntity>(userEntities);

        //打印分页信息
        System.out.println("数据总数：" + pageInfo.getTotal());
        System.out.println("数据总页数：" + pageInfo.getPages());
        System.out.println("进入第一页：" + pageInfo.getNavigateFirstPage());
        System.out.println("进入最后一页：" + pageInfo.getNavigateLastPage());

        for (UserEntity user : pageInfo.getList()) {
            System.out.println(user);
        }
        return pageInfo;
    }

    //更新数据-改数据
    @Override
    public int updateUser(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return this.userDao.updateUser(userEntity);
    }

    //删除数据
    @Override
    public int delUser(Integer id) {
        return this.userDao.delUser(id);
    }

}

```
接口层：spring5x-dubbo-api模块中
```java
package com.zja.service;


import com.github.pagehelper.PageInfo;
import com.zja.model.dto.UserEntityDTO;
import com.zja.model.entity.UserEntity;

import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/14
 */
public interface UserService {

    //静态插入数据:通用方法
    int insertUser(UserEntity userEntity);

    //动态插入数据: mysql用法，id自增
    int insertUserMysql(UserEntity userEntity);
    //动态插入数据:oracle用法，id使用序列
    int insertUserOracle(UserEntity userEntity);

    //mybatis批量插入数据:mysql用法，id自增
    int mysqlBatchSaveUser(List<UserEntity> userEntities);
    //mybatis批量插入数据:oracle用法，id使用序列
    int oracleBatchSaveUser(List<UserEntity> userEntities);

    //按id查询用户
    UserEntityDTO queryUserById(Integer id);
    //查询所有用户
    List<UserEntity> queryAllUser();

    //获取分页结果
    List<UserEntity> getPagingResults(int pageNum, int pageSize);
    //获取分页结果及分页信息
    PageInfo<UserEntity> queryPageInfo(int pageNum, int pageSize);

    //更新数据-改数据
    int updateUser(UserEntity userEntity);

    //删除数据
    int delUser(Integer id);
}

```

### 3、web层调用
```java
package com.zja.controller;

import com.github.pagehelper.PageInfo;
import com.zja.model.entity.UserEntity;
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
@RequestMapping("rest/dubbo")
@Api(tags = {"DubboMybatisController"}, description = "dubbo简单测试")
public class DubboMybatisController {

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
            userEntity.setId(100+i);
            userEntity.setUserName("Zhengja_"+i);
            userEntity.setAge(20+i);
            userEntity.setCreateTime(new Date());
            entityList.add(userEntity);
        }
        return this.userService.oracleBatchSaveUser(entityList);
    }

    @GetMapping("queryUserById")
    @ApiOperation(value = "按id查询用户", notes = "按id查询数据", httpMethod = "GET")
    public Object queryUserById(@RequestParam Integer id) {
        return this.userService.queryUserById(id);
    }

    @GetMapping("queryAllUser")
    @ApiOperation(value = "查询所有用户", notes = "查询所有数据", httpMethod = "GET")
    public List<UserEntity> queryAllUser() {
        return this.userService.queryAllUser();
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
    public int delUser(@RequestParam Integer id) {
        return this.userService.delUser(id);
    }

}

```

### 4、项目的github和简书博客地址
**github:**
- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)

**博客:**
- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts
