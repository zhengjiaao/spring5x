# Spring5x-mybatis-druid-base

[TOC]

Spring5x-mybatis-druid-base此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请先参考：  [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)

**Spring5x-mybatis-druid-base 是一个mybatis基础模块，今后的spring+mybatis 的xml配置方式，在此模块上扩展。**



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

* 1、集成 druid + mysql(oracle)
* 2、集成 mybatis 配置
* 3、mapper 配置：
  - mybatis 增删改查操作
  - 动态(trim) 插入数据
  - mybatis+mysql 批量插入数据
  - mybatis+oracle 批量插入数据
* 4、Mybatis-PageHelper 分页插件



### 1、集成 druid + mysql(oracle)

****

pom.xml 引入依赖 ，使用的是mysql

```xml
<properties>
        <!--spring5.x 至少需要jdk1.8及以上版本-->
        <spring.version>5.0.9.RELEASE</spring.version>
</properties>

        <!--jdbc 相关依赖包 上面已经引入了-->
        <!--<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>-->

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

        <!--druid 依赖-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.20</version>
        </dependency>

```

**web.xml 配置druid**

```xml
	<!-- 配置 Druid 监控信息显示页面 访问地址 <a href="http://localhost:8080/项目路径/druid/index.html"> -->
    <servlet>
        <servlet-name>DruidStatView</servlet-name>
        <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
        <init-param>
            <!-- 允许清空统计数据 -->
            <param-name>resetEnable</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <!-- 用户名 -->
            <param-name>loginUsername</param-name>
            <param-value>druid</param-value>
        </init-param>
        <init-param>
            <!-- 密码 -->
            <param-name>loginPassword</param-name>
            <param-value>druid</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>DruidStatView</servlet-name>
        <url-pattern>/druid/*</url-pattern>
    </servlet-mapping>

    <!--配置 WebStatFilter 用于采集 web-jdbc 关联监控的数据-->
    <filter>
        <filter-name>DruidWebStatFilter</filter-name>
        <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
        <init-param>
            <param-name>exclusions</param-name>
            <param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>DruidWebStatFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

```

**jdbc.properties**

```properties
# mysql 数据库配置:
mysql.jdbc.driverClassName=com.mysql.jdbc.Driver
mysql.jdbc.url=jdbc:mysql://127.0.0.1:3306/test?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
mysql.jdbc.username=root
mysql.jdbc.password=123456
mysql.jdbc.validationQuery=select 'x'


# oracle 数据库配置:
oracle.jdbc.driverClassName=oracle.jdbc.driver.OracleDriver
oracle.jdbc.url=jdbc:oracle:thin:@127.0.0.1/orcl
oracle.jdbc.username=duke
oracle.jdbc.password=duke
oracle.jdbc.validationQuery=select 'x' from dual

```

**spring-druid.xml **

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">
    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:properties/jdbc.properties" ignore-unresolvable="true"/>

    <!--配置 druid 数据源 关于更多的配置项 可以参考官方文档 <a href="https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8" > -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
        <!-- 基本属性 url、user、password ，driverClassName默认自动识别-->
        <!--配置mysql -->
        <property name="url" value="${mysql.jdbc.url}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>


        <!--配置oracle -->
        <!--<property name="url" value="${oracle.jdbc.url}"/>
        <property name="username" value="${oracle.jdbc.username}"/>
        <property name="password" value="${oracle.jdbc.password}"/>
        <property name="validationQuery" value="${oracle.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${oracle.jdbc.driverClassName}"/>-->


        <!--validationQuery 用来检测连接是否有效的 sql，要求是一个查询语句，常用 select 'x'。
            但是在 oracle 数据库下需要写成 select 'x' from dual 不然实例化数据源的时候就会失败,
            这是由于 oracle 和 mysql 语法间的差异造成的-->
        <!--mysql-->
        <!--<property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>-->
        <!--oracle-->
        <!--<property name="validationQuery" value="${oracle.jdbc.validationQuery}"/>-->

        <!-- 配置初始化大小、最小、最大连连接数量 -->
        <property name="initialSize" value="10"/>
        <property name="minIdle" value="10"/>
        <property name="maxActive" value="200"/>

        <!-- 配置获取连接等待超时的时间 -->
        <property name="maxWait" value="60000"/>

        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="60000"/>

        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="600000"/>
        <!-- 配置一个连接在池中最大生存的时间，单位是毫秒 -->
        <property name="maxEvictableIdleTimeMillis" value="900000"/>

        <!--建议配置为 true，不影响性能，并且保证安全性。申请连接的时候检测，
        如果空闲时间大于 timeBetweenEvictionRunsMillis，执行 validationQuery 检测连接是否有效。-->
        <property name="testWhileIdle" value="true"/>
        <!--申请连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能。-->
        <property name="testOnBorrow" value="false"/>
        <!--归还连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能。-->
        <property name="testOnReturn" value="false"/>

        <!--连接池中的 minIdle 数量以内的连接，空闲时间超过 minEvictableIdleTimeMillis，则会执行 keepAlive 操作。-->
        <property name="keepAlive" value="true"/>
        <property name="phyMaxUseCount" value="100000"/>

        <!-- 配置监控统计拦截的 filters Druid 连接池的监控信息主要是通过 StatFilter 采集的，
        采集的信息非常全面，包括 SQL 执行、并发、慢查、执行时间区间分布等-->
        <property name="filters" value="stat,wall"/>
    </bean>

</beans>

```

> 注：MySQL和Oracle 数据库更换方式：
> 只需要将spring-druid.xml 中 "配置mysql" 和 "配置oracle" 注释其中一个。



**spring-mvc.xml**

```xml
	<!--资源 druid.xml 配置-->
    <import resource="classpath:META-INF/spring/spring-druid.xml"/>

```





### 2、集成 mybatis

****

pom.xml  引入 mybatis和 分页插件配置

```xml
        <!--mybatis 依赖包-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.2</version>
        </dependency>
        <!--Mybatis-PageHelper分页插件:目前支持Oracle,Mysql,MariaDB,SQLite,Hsqldb,PostgreSQL等等常用数据库分页-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>5.1.2</version>
        </dependency>

```

mybatis-config.xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">


<!--mybatis配置顺序说明-->
<!--
配置文件中的位置必须符合要求，否则会报错，顺序如下:
properties?, settings?,
typeAliases?, typeHandlers?,
objectFactory?,objectWrapperFactory?,
 plugins?,
environments?, databaseIdProvider?, mappers?
-->

<!-- mybatis 配置文件 -->
<configuration>
    <settings>
        <!-- 开启驼峰命名 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 打印查询 sql -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>

    <!-- 配置实体类的别名 -->
    <typeAliases>
        <package name="com.zja.entity"/> <!-- 默认实体类的别名:类名 推荐使用类名首字母小写 -->
    </typeAliases>

    <!--Mybatis-PageHelper分页插件-->
    <plugins>
        <!-- com.github.pagehelper为PageHelper类所在包名 -->
        <plugin interceptor="com.github.pagehelper.PageInterceptor">
            <!-- 配置属性:默认可不配置,属性配置都是可选的-->

            <!-- 大于3.3.0版本可用 - 分页参数合理化，默认false禁用 -->
            <!-- 启用合理化时，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页 -->
            <!-- 禁用合理化时，如果pageNum<1或pageNum>pages会返回空数据 -->
            <!--<property name="reasonable" value="true"/>-->
            <!-- 方言：默认自动识别，设置数据库类型 Oracle,Mysql,MariaDB,SQLite,Hsqldb,PostgreSQL等等常用数据库-->
            <!--<property name="dialect" value="mysql"/>-->
            <!-- 该参数默认为false -->
            <!-- 设置为true时，使用RowBounds分页会进行count查询 -->
            <!--<property name="rowBoundsWithCount" value="true"/>-->
        </plugin>
    </plugins>

</configuration>

```

spring-mvc.xml 配置 

```xml

    <!--配置 mybatis 会话工厂 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--指定 mapper 文件所在的位置-，可以自建mappers文件夹 -->
        <property name="mapperLocations" value="classpath*:/META-INF/spring/mappers/**/*.xml"/>
        <property name="configLocation" value="classpath:META-INF/spring/mybatis-config.xml"/>
    </bean>

    <!--扫描注册接口 -->
    <!--作用:从接口的基础包开始递归搜索，并将它们注册为 MapperFactoryBean(只有至少一种方法的接口才会被注册;, 具体类将被忽略)-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--指定会话工厂 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!-- 指定 mybatis 接口所在的包 -->
        <property name="basePackage" value="com.zja.dao"/>
    </bean>

    <!--定义事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 开启事务注解@Transactional 支持 -->
    <tx:annotation-driven/>

```

UserEntity.java 实体类 

```java
package com.zja.entity;

import java.util.Date;

/**
 * @author ZhengJa
 * @description User 对象
 * @data 2019/10/29
 */
public class UserEntity {
    private String name;
    private String age;
    private Date date;

    public UserEntity() {
    }

    public UserEntity(String name, String age,Date date) {
        this.name = name;
        this.age = age;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", date=" + date +
                '}';
    }
}

```

> 建表语句：在项目中db文件夹下，含oracle和mysql

UserDao.java 接口 

```java
package com.zja.dao;

import com.zja.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ZhengJa
 * @description UserDao 接口
 * @data 2019/10/29
 */
public interface UserDao {

    //静态插入数据:通用方法
    int insertUser(UserEntity userEntity);

    //动态插入数据: mysql用法，id自增
    int insertUserMysql(UserEntity userEntity);
    //动态插入数据:oracle用法，id使用序列
    int insertUserOracle(UserEntity userEntity);

    //mybatis批量插入数据:mysql用法，id自增
    int mysqlBatchSaveUser(@Param("userEntityList") List<UserEntity> userEntities);
    //mybatis批量插入数据:oracle用法，id使用序列
    int oracleBatchSaveUser(@Param("userEntityList") List<UserEntity> userEntities);

    //按id查询用户
    UserEntity queryUserById(Integer id);
    //查询所有用户
    List<UserEntity> queryAllUser();

    //更新数据-改数据
    int updateUser(UserEntity userEntity);

    //删除数据
    int delUser(Integer id);

}

```

### 3、mapper 配置：

****

resources/mappers文件夹下新建 UserEntity.xml，内容如下

```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.zja.dao.UserDao">

    <!--构造函数-->
    <resultMap id="BaseResultMap" type="com.zja.entity.UserEntity">
        <id column="id" property="id" jdbcType="INTEGER"/>
        <result column="username" property="userName" jdbcType="VARCHAR"/>
        <result column="age" property="age" jdbcType="VARCHAR"/>
        <result column="createtime" property="createTime" jdbcType="DATE"/>
    </resultMap>

    <!--记录计数：用于分页-->
    <resultMap id="recordCounts" type="java.lang.Long">
        <result column="recordCounts" jdbcType="BIGINT"/>
    </resultMap>

    <!--sql语句字段-->
    <sql id="Base_Column_List">
        id, username, age, createtime
    </sql>

    <!--按id查询某个用户信息-->
    <select id="queryUserById" resultType="userEntity">
        select
        <include refid="Base_Column_List"/>
        from userentity t where t.id=#{id}
    </select>

    <!--查询所有数据-->
    <select id="queryAllUser" resultMap="BaseResultMap">
        select * from userentity
    </select>

    <!--静态插入数据:所有数据库通用方法-->
    <insert id="insertUser" parameterType="userEntity">
        insert into userentity(id, username, age, createtime)
        values (#{id,jdbcType=INTEGER}, #{userName,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER}, #{createTime,jdbcType=DATE})
    </insert>

    <!--动态(trim)插入数据：使用 mysql id自增-->
    <insert id="insertUserMysql" parameterType="userEntity">
        insert into userentity
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="id != null and id !=0 ">
                id,
            </if>
            <if test='userName != null and userName != "" '>
                username,
            </if>
            <if test="age != null and age !=0 ">
                age,
            </if>
            <if test="createTime != null">
                createtime,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="id != null and id != 0 ">
                #{id,jdbcType=INTEGER},
            </if>
            <if test='userName != null and userName != "" '>
                #{userName,jdbcType=VARCHAR},
            </if>
            <if test="age != null and age !=0 ">
                #{age,jdbcType=INTEGER},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=DATE},
            </if>
        </trim>
    </insert>

    <!--动态(trim)插入数据： 使用 oracle id序列为SEQ_MY_HIBERNATE -->
    <insert id="insertUserOracle" parameterType="userEntity">
        insert into userentity
        <trim prefix="(" suffix=")" suffixOverrides=",">
            id,
            <if test='userName != null and userName != "" '>
                username,
            </if>
            <if test="age != null and age !=0 ">
                age,
            </if>
            <if test="createTime != null">
                createtime,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            SEQ_MY_USER_HIBERNATE.NEXTVAL,
            <if test='userName != null and userName != "" '>
                #{userName,jdbcType=VARCHAR},
            </if>
            <if test="age != null and age !=0 ">
                #{age,jdbcType=INTEGER},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=DATE},
            </if>
        </trim>
    </insert>

    <!--mybatis+mysql批量插入数据：使用 mysql id自增-->
    <insert id="mysqlBatchSaveUser" parameterType="java.util.List" useGeneratedKeys="false">
        insert into userentity(username, age, createtime) values
        <foreach collection="userEntityList" item="item" index="index" separator=",">
        (#{item.userName}, #{item.age}, #{item.createTime})
        </foreach>
    </insert>

    <!--mybatis+oracle批量插入数据： oracle id不使用序列自增 -->
    <!--oracle+mybatis批量插入遇到问题：（已解决）
        1、id不能使用序列,没有提交之前，是无法获取下次序列的递增值，导致主键唯一约束异常
        2、oracle+mybatis批量插入报错的解决办法（命令未执行结束）-->
    <insert id="oracleBatchSaveUser" parameterType="java.util.List">
        insert into userentity
        (id, username, age, createtime)
        select t.* from
        (
        <foreach collection="userEntityList" item="item" separator="union all">
            select #{item.id}, #{item.userName}, #{item.age}, #{item.createTime} from dual
        </foreach>
        ) t
    </insert>

    <!--动态更新数据-->
    <update id="updateUser" parameterType="userEntity">
        update userentity
        <set>
            <if test='userName != null and userName != "" '>
                username = #{userName},
            </if>
            <if test="age != null and age !=0">
                age = #{age},
            </if>
            <if test="createTime != null">
                createtime = #{createTime},
            </if>
        </set>
        where id=#{id}
    </update>

    <!--删除数据-->
    <delete id="delUser">
        delete from userentity where id=#{id}
    </delete>

</mapper>

```

MybatisController.java 

```java
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
    public UserEntity queryUserById(@RequestParam Integer id) {
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



**接口测试：**

- 1、直接使用浏览器调用接口传参
- 2、使用postman工具
- 3、使用swagger api 测试(此项目引入了swagger)



**druid测试：**

- 访问：http://localhost:8080/项目路径/druid/index.html



**到此所有的配置已完成！** 下面时配置文件的完整版！



## pom.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zja</groupId>
    <artifactId>spring5x-mybatis-druid-base</artifactId>
    <packaging>war</packaging>

    <name>spring5x-mybatis-druid-base</name>

    <!--说明：spring5.x-base模块是spring5.x基础框架，其它模块都是以此模块为基础扩展的-->
    <properties>
        <!--spring5.x 至少需要jdk1.8及以上版本-->
        <spring.version>5.0.9.RELEASE</spring.version>
        <!--jdk必须 >=1.8-->
        <jdk.version>1.8</jdk.version>
        <!--maven 版本-->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.plugin.version>3.6.0</maven.compiler.plugin.version>
        <mavne.surefire.plugin.version>2.19.1</mavne.surefire.plugin.version>
        <maven-war-plugin.version>2.6</maven-war-plugin.version>
        <servlet.version>4.0.1</servlet.version>

        <!--spring5.x集成swagger2-->
        <springfox.version>2.9.2</springfox.version>
    </properties>

    <dependencies>
        <!--spring核心包——Start-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-oxm</artifactId>
            <version>${spring.version}</version>
        </dependency>
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
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>
        <!--spring核心包——End-->

        <!--servlet-api  web层-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet.version}</version>
            <scope>provided</scope>
        </dependency>

        <!--jackson 类序列化-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.4</version>
            <exclusions>
                <exclusion>
                    <artifactId>jackson-annotations</artifactId>
                    <groupId>com.fasterxml.jackson.core</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.4</version>
        </dependency>

        <!--日志，修复日志-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-nop</artifactId>
            <version>1.7.28</version>
        </dependency>

        <!--spring5.x 集成swagger2-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>${springfox.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>jackson-annotations</artifactId>
                    <groupId>com.fasterxml.jackson.core</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-context</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-beans</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-aop</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>slf4j-api</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>${springfox.version}</version>
        </dependency>


        <!--jdbc 相关依赖包 上面已经引入了-->
        <!--<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>-->

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

        <!--druid 依赖-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.20</version>
        </dependency>

        <!--mybatis 依赖包-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.2</version>
        </dependency>
        <!--Mybatis-PageHelper分页插件:目前支持Oracle,Mysql,MariaDB,SQLite,Hsqldb,PostgreSQL等等常用数据库分页-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>5.1.2</version>
        </dependency>

    </dependencies>

    <build>
        <finalName>spring5x-mybatis-druid-base</finalName>
        <plugins>
            <!--maven的编译插件-->
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven.compiler.plugin.version}</version>
                <configuration>
                    <!--开发版本-->
                    <source>${jdk.version}</source>
                    <!--.class文件版本-->
                    <target>${jdk.version}</target>
                    <!--打包后的编码-->
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
            <!--打包跳过测试-->
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${mavne.surefire.plugin.version}</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

spring-mvc.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.zja.*"/>

    <!--使用默认的 Servlet 来响应静态文件 -->
    <mvc:default-servlet-handler/>

    <!-- 开启springMVC 注解驱动 -->
    <mvc:annotation-driven>
        <mvc:message-converters register-defaults="false">
            <!-- 将StringHttpMessageConverter的默认编码设为UTF-8 ，解决返回给前端中文乱码-->
            <bean class="org.springframework.http.converter.StringHttpMessageConverter">
                <constructor-arg value="UTF-8"/>
            </bean>
            <!-- 将Jackson2HttpMessageConverter的默认格式化输出设为true -->
            <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
                <property name="prettyPrint" value="true"/>
                <property name="supportedMediaTypes">
                    <list>
                        <!-- 优先使用该媒体类型,为了解决IE浏览器下,返回JSON数据的下载问题 -->
                        <value>application/json;charset=UTF-8</value>
                        <value>text/html;charset=UTF-8</value>
                        <value>text/json;charset=UTF-8</value>
                    </list>
                </property>
                <!-- 使用内置日期工具进行处理 -->
                <property name="objectMapper">
                    <bean class="com.fasterxml.jackson.databind.ObjectMapper">
                        <property name="dateFormat">
                            <bean class="java.text.SimpleDateFormat">
                                <constructor-arg type="java.lang.String" value="yyyy-MM-dd"/>
                            </bean>
                        </property>
                    </bean>
                </property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>

    <!-- 增加application.properties文件 -->
    <context:property-placeholder
            location="classpath:properties/application.properties" ignore-unresolvable="true"/>

    <!--资源 druid.xml 配置-->
    <import resource="classpath:META-INF/spring/spring-druid.xml"/>

    <!--配置 mybatis 会话工厂 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--指定 mapper 文件所在的位置-->
        <property name="mapperLocations" value="classpath*:/mappers/**/*.xml"/>
        <property name="configLocation" value="classpath:META-INF/spring/mybatis-config.xml"/>
    </bean>

    <!--扫描注册接口 -->
    <!--作用:从接口的基础包开始递归搜索，并将它们注册为 MapperFactoryBean(只有至少一种方法的接口才会被注册;, 具体类将被忽略)-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--指定会话工厂 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!-- 指定 mybatis 接口所在的包 -->
        <property name="basePackage" value="com.zja.dao"/>
    </bean>

    <!--定义事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 开启事务注解@Transactional 支持 -->
    <tx:annotation-driven/>

    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
          id="internalResourceViewResolver">
        <!-- 前缀 ：/WEB-INF/jsp/和/WEB-INF/html/-->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!-- 后缀 ：.jsp和.html-->
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>

```

web.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--配置spring前端控制器-->
    <servlet>
        <servlet-name>springMvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:META-INF/spring/spring-mvc.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>springMvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- 配置 Druid 监控信息显示页面 访问地址 <a href="http://localhost:8080/项目路径/druid/index.html"> -->
    <servlet>
        <servlet-name>DruidStatView</servlet-name>
        <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
        <init-param>
            <!-- 允许清空统计数据 -->
            <param-name>resetEnable</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <!-- 用户名 -->
            <param-name>loginUsername</param-name>
            <param-value>druid</param-value>
        </init-param>
        <init-param>
            <!-- 密码 -->
            <param-name>loginPassword</param-name>
            <param-value>druid</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>DruidStatView</servlet-name>
        <url-pattern>/druid/*</url-pattern>
    </servlet-mapping>

    <!--配置 WebStatFilter 用于采集 web-jdbc 关联监控的数据-->
    <filter>
        <filter-name>DruidWebStatFilter</filter-name>
        <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
        <init-param>
            <param-name>exclusions</param-name>
            <param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>DruidWebStatFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!--Encoding configuration-->
    <filter>
        <filter-name>encoding</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encoding</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>

```

**完整配置已发完！**



1、druid测试：

- 访问：http://localhost:8080/项目路径/druid/index.html

2、swagger api接口访问测试:

- 访问：http://localhost:8080/项目路径/swagger-ui.html



## github 地址：
* [https://github.com/zhengjiaao/spring5x](https://github.com/zhengjiaao/spring5x)


## 博客地址
* 简书：https://www.jianshu.com/u/70d69269bd09
* 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts