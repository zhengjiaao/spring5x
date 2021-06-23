# spring5x-dubbo-service
[toc]

spring5x-dubbo-service此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构
如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、spring集成 dubbo依赖和xml配置
- 2、实体类和接口层
- 3、项目的github和博客地址

**项目架构：spring5.x+mybatis+druid/c3p0+mysql/oracle+dubbo**
功能：spring项目启动自动执行sql文件； dozer映射功能

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
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.13</version>
        </dependency>

```

spring-dubbo-provider.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <!--定义当前应用名称，主要用于注册中心的信息保存，这个名称可以任意填写-->
    <dubbo:application name="${dubbo.application.name}"/>
    <!--定义dubbo注册中心的地址-->
    <dubbo:registry protocol="zookeeper" address="${dubbo.registry.address}"/>
    <!--定义dubbo所在服务执行时暴露给客户端的端口-->
    <dubbo:protocol name="dubbo" port="${dubbo.protocol.port}"/>
    <!--定义远程服务提供者操作的超时时间-->
    <dubbo:provider timeout="${dubbo.provider.timeout}"/>

    <!--定义dubbo远程服务的接口,声明需要暴露的服务接口
        interface 定义的接口
        ref Service 中对应实现类首字母小写
        version是需要考虑到版本一致问题-->
    <dubbo:service interface="com.zja.service.CascadeService" ref="cascadeServiceImpl" version="${dubbo.interface.version}"/>
    <dubbo:service interface="com.zja.service.UserService" ref="userServiceImpl" version="${dubbo.interface.version}"/>

    <!-- 和本地 bean 一样实现服务 -->
    <bean id="cascadeService" class="com.zja.service.CascadeService" abstract="true"/>
    <bean id="userService" class="com.zja.service.UserService" abstract="true"/>

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


### 3、项目的github和简书博客地址
**github:**
- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)

**博客:**
- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts
