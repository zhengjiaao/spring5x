# spring5x-jdbc-database

[toc]

spring5x-jdbc-database 此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构
如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、jdbc主要依赖和配置
- 2、雪花算生成法递增趋势id
- 3、JdbcTemplate操作数据库


### 1、jdbc主要依赖和配置

****

pom.xml 依赖

```xml
        <!-- spring的jdbc开发所需jar包：tx，jdbc -->
        <!-- spring的事务管理所需的jar：tx -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <!--mysql 连接驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.13</version>
        </dependency>
        <!--oracle 连接驱动-->
        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc6</artifactId>
            <version>11.2.0.3</version>
        </dependency>

```
jdbc.properties
```properties
# mysql 数据库配置:需要修改用户/密码
mysql.jdbc.driverClassName=com.mysql.cj.jdbc.Driver
mysql.jdbc.url=jdbc:mysql://127.0.0.1:3306/test?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
mysql.jdbc.username=root
mysql.jdbc.password=123456
mysql.jdbc.validationQuery=select 'x'

# oracle 数据库配置:需要修改用户/密码
oracle.jdbc.driverClassName=oracle.jdbc.driver.OracleDriver
oracle.jdbc.url=jdbc:oracle:thin:@127.0.0.1/orcl
oracle.jdbc.username=duke
oracle.jdbc.password=duke
oracle.jdbc.validationQuery=select 'x' from dual

```
spring-jdbc.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--jdbc据库连接资源-->
    <context:property-placeholder location="classpath:properties/jdbc.properties" ignore-unresolvable="true"/>

    <!--可选 Mysql/Oracle 数据源-->

    <!--Mysql 数据源-->
    <bean id="dataSource"
          class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName"
                  value="${mysql.jdbc.driverClassName}">
        </property>
        <property name="url"
                  value="${mysql.jdbc.url}">
        </property>
        <property name="username" value="${mysql.jdbc.username}"></property>
        <property name="password" value="${mysql.jdbc.password}"></property>
    </bean>

    <!--Oracle 数据源-->
    <!--<bean id="dataSource"
          class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName"
                  value="${oracle.jdbc.driverClassName}">
        </property>
        <property name="url"
                  value="${oracle.jdbc.url}">
        </property>
        <property name="username" value="${oracle.jdbc.username}"></property>
        <property name="password" value="${oracle.jdbc.password}"></property>
    </bean>-->

    <!--jdbc操作数据库模板-->
    <bean id="jdbcTemplate"
          class="org.springframework.jdbc.core.JdbcTemplate" abstract="false"
          lazy-init="false">
        <property name="dataSource">
            <ref bean="dataSource"/>
        </property>
    </bean>
</beans>

```



### 2、雪花算生成法递增趋势id
SnowFlake.java
```java
package com.zja.util;

/**
 * Desc：雪花算法：分布式自增ID(是递增趋势，非递增)雪花算法snowflake (Java版)
 */
public class SnowFlake {
    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2, 3);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            System.out.println(snowFlake.nextId());
        }

        //100万个id大概5~12秒
        System.out.println(System.currentTimeMillis() - start);


    }
}

```

自定义生成递增趋势id，目的支持跨数据库
```java
/**
 * Desc：自定义生成递增趋势id，目的支持跨数据库
 */
@Component
public class MyId {

    public long generateId() {
        SnowFlake snowFlake = new SnowFlake(2, 3);

        System.out.println(snowFlake.nextId());
        return snowFlake.nextId();
    }
}

```

### 3、JdbcTemplate操作数据库
```java
package com.zja.service.Impl;

import com.zja.entity.UserEntity;
import com.zja.rowmapper.UserEntityRowMapper;
import com.zja.service.UserService;
import com.zja.util.MyId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date: 2019-11-28 15:59
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：用户操作：jdbc模板增删改查
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MyId myId;

    /**
     * 增加用户(将用户保存到数据库)
     *
     * @param
     */
    @Override
    public Object saveUser(String username, int age) {
        UserEntity userByName = (UserEntity) this.findUserByName(username);

        if (userByName == null) {
            String sql = "insert into userentity (id,username,age) values(?,?,?)";
            Object[] args = {myId.generateId(),username, age};
            int insert = this.jdbcTemplate.update(sql, args);
            if (insert > 0) {
                return "新增用户成功!!!";
            }
        }
        return "用户已存在,请改用其它用户名称!";
    }

    /**
     * 根据用户名称修改用户年龄(修改库中用户信息)
     *
     * @param
     */
    @Override
    public Object updateUser(String username, int age) {
        UserEntity userByName = (UserEntity) this.findUserByName(username);
        if (userByName != null) {
            //sql语句
            String sql = "update userentity  SET age = ? WHERE username = ?";
            Object[] args = {age, username};
            int update = jdbcTemplate.update(sql, args);
            if (update > 0) {
                return "修改用户年龄成功!!!";
            }
        }
        return "用户名称不存在,修改年龄失败!";
    }

    /**
     * 根据用户名查询用户信息(从数据库中获取用户信息)
     *
     * @param username 用户名称
     */
    @Override
    public Object findUserByName(String username) {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity where username=?", new BeanPropertyRowMapper<UserEntity>(UserEntity.class), username);
        if (query.isEmpty()) {
            return null;
        } else if (query.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        } else {
            return query.get(0);
        }
    }

    /**
     * 查询所有用户信息
     */
    @Override
    public Object getAllUser() {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity", new BeanPropertyRowMapper<UserEntity>(UserEntity.class));
        if (query.isEmpty()) {
            return null;
        }else {
            return query;
        }
    }

    /**
     * 根据用户名查询用户信息(从数据库中获取用户信息)
     *
     * @param username 用户名称
     */
    @Override
    public Object findUserByName2(String username) {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity where username=?", new UserEntityRowMapper(), username);
        if (query.isEmpty()) {
            return null;
        } else if (query.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        } else {
            return query.get(0);
        }
    }

    /**
     * 查询所有用户信息
     */
    @Override
    public Object getAllUser2() {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity", new UserEntityRowMapper());
        if (query.isEmpty()) {
            return null;
        }else {
            return query;
        }
    }

    /**
     * 根据用户名称删除用户(删除数据库中用户)
     *
     * @param username 用户名称
     */
    @Override
    public Object deleteUserByName(String username) {
        String sql = "delete from userentity where username = ?";
        int update = this.jdbcTemplate.update(sql, username);
        if (update > 0) {
            return "删除成功!!!";
        }
        return "删除失败!!!";
    }

    @Override
    public Object deleteAllUser() {
        String sql = "delete from userentity";
        int update = this.jdbcTemplate.update(sql);
        if (update > 0) {
            return "删除所有用户成功!!!";
        }
        return "删除所有用户成功!!!";
    }
}

```



## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)



## 博客地址

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts

