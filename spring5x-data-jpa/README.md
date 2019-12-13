# spring5x-data-jpa
[toc]

spring5x-data-jpa此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构
如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、spring集成 jpa依赖和配置
- 2、实体类和dao接口层
- 3、单元测试(jpa使用)
- 4、项目完整pom
- 5、项目的github和博客地址

**项目架构：spring5.x+jpa+druid/c3p0+mysql/oracle**

### 1、spring集成 jpa依赖和配置
- jpa依赖
```xml
        <!-- hibernate 框架 -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>5.4.4.Final</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>5.4.4.Final</version>
        </dependency>
        <!--hibernate或jpa所需依赖,不然会找不到session工厂或transaction -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!--spring-data-jpa-->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-jpa</artifactId>
            <version>2.1.10.RELEASE</version>
        </dependency>
```
- jpa配置

hibernate-jpa.properties
```properties
## hibernate扫描包
hibernate.scan.package=com.zja.entity

# 关系数据库驱动方言,不写自动识别(可选)
#hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
#hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 控制台是否打印sql语句
hibernate.show_sql=true
# 控制台是否打印sql语句是否格式化
hibernate.format_sql=true
# 启动更新表结构update，none不用此功能
hibernate.hbm2ddl.auto=update
# 建表的命名规则： My_NAME->MyName
hibernate.ejb.naming_strategy=org.hibernate.cfg.ImprovedNamingStrategy
#hibernate.ejb.naming_strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
##  开发阶段建议打开，输出所有SQL语句到控制台
adapter.show_sql=true
## 开发阶段建议打开，自动更新数据库表结构
adapter.generate_ddl=true

```

spring-data-jpa.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/data/jpa https://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

    <!-- 数据源配置 -->
    <!--druid.xml 配置-->
    <!--<import resource="classpath:META-INF/spring/datasource/spring-druid.xml"/>-->
    <!--c3p0.xml 配置-->
    <import resource="classpath:META-INF/spring/datasource/spring-c3p0.xml"/>

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:properties/hibernate-jpa.properties" ignore-unresolvable="true"/>

    <!-- 配置JPA适配器,实现厂商的特定属性-->
    <bean id="hibernateJpaVendorAdapter" class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
        <property name="showSql" value="${adapter.show_sql}"/>
        <property name="generateDdl" value="${adapter.generate_ddl}"/>
        <!--关系数据库驱动方言，不写自动识别-->
        <!--<property name="databasePlatform" value="${hibernate.dialect}"/>-->
    </bean>

    <!-- 定义实体管理器工厂 Jpa配置 LocalContainerEntityManagerFactoryBean这个选项Spring扮演了容器的角色。完全掌管JPA -->
    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <!-- 指定数据源 -->
        <property name="dataSource" ref="dataSource"/>
        <!-- 指定Jpa持久化实现厂商类,这里以Hibernate为例 -->
        <property name="jpaVendorAdapter" ref="hibernateJpaVendorAdapter"/>
        <!-- 指定Entity实体类包路径 -->
        <property name="packagesToScan" value="${hibernate.scan.package}"/>
        <!-- 指定JPA属性；如Hibernate中指定是否显示SQL的是否显示、方言等 -->
        <property name="jpaProperties">
            <props>
                <!--关系数据库驱动方言，不写自动识别-->
                <!--<prop key="hibernate.dialect">${hibernate.dialect}</prop>-->
                <!--控制台是否打印sql语句-->
                <prop key="hibernate.show_sql">${hibernate.show_sql}</prop>
                <!--sql语句格式化-->
                <prop key="hibernate.format_sql">${hibernate.format_sql}</prop>
                <!--启动更新表结构，none不用此功能-->
                <prop key="hibernate.hbm2ddl.auto">${hibernate.hbm2ddl.auto}</prop>
                <!-- 建表的命名规则： My_NAME->MyName-->
                <prop key="hibernate.ejb.naming_strategy">${hibernate.ejb.naming_strategy}</prop>
            </props>
        </property>
    </bean>

    <!--扫描dao包-->
    <!--<jpa:repositories base-package="com.zja.dao" entity-manager-factory-ref="entityManagerFactory" transaction-manager-ref="transactionManager" />-->
    <jpa:repositories base-package="com.zja.dao"/>

    <!-- Jpa 事务配置 -->
    <bean id="transactionManager"  class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>

    <!--XML配置事务声明方式 开启注解声明事务 -->
    <tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true" />

</beans>

```
>1.修改数据源`dataSource`配置参考github项目，或使用自己的数据源
>2.需要修改实体类扫描包和dao接口扫描包路径


### 2、实体类和dao接口层
- 实体类
```java
package com.zja.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Date: 2019-12-12 16:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Data
@Entity
@Table(name = "demojpa")
public class DemoJpa implements Serializable {

    /**默认 序列名称 HIBERNATE_SEQUENCE
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * 主键生成策略：1.AUTO 由程序控制 2.SEQUENCE 由序列，数据库支持（如oracle）
     * 3.IDENTITY 主键由数据库自动生成（主要是自动增长型如mysql） 4.TABLE：使用一个特定的数据库表格来保存主键
     *
     * 注意：第一次启动项目，mysql数据库jpa自动创建默认hibernate_sequence序列，
     *      oracle不自动创建默认序列，需要手动创建序列HIBERNATE_SEQUENCE
     */
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue
    private int id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column
    private String email;
    private int age;
}


package com.zja.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhengJa
 * @description User 实体类
 * @data 2019/10/29
 */
@Data
@Entity
@Table(name = "userentity")
@ApiModel("用户信息实体类")
public class UserEntity implements Serializable {

    @ApiModelProperty(value = "默认:mysql自增,oracle序列")
    @Id
    @Column(name = "id", nullable = false)
    //oracle序列，默认 HIBERNATE_SEQUENCE
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue
    private Long id;
    @ApiModelProperty("用户名")
    @Basic(fetch = FetchType.EAGER,optional = false) //急加载，属性是否允许为null
    @Column(name = "username", nullable = false, length = 225)
    private String userName;
    @ApiModelProperty("年龄")
    @Column(name = "age", nullable = true)
    private Integer age;
    @ApiModelProperty("不传值,后台创建时间")
    @Column(name = "createtime", nullable = true)
    private Date createTime;
    @ApiModelProperty("不传值,后台创建时间")
    @Column(name = "updatetime", nullable = true)
    private Date updateTime;
}


```
dao接口层
DemoJpaRepositories.java
```java
package com.zja.dao;

import com.zja.entity.DemoJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Date: 2019-12-12 17:16
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface DemoJpaRepositories extends JpaRepository<DemoJpa,Integer> {

    //根据firstName与LastName查找(两者必须在数据库有)
    DemoJpa findByFirstNameAndLastName(String firstName, String lastName);

    //根据firstName或LastName查找(两者其一有就行)
    DemoJpa findByLastNameOrFirstName(String lastName,String firstName);

    //根据firstName查找它是否存在数据库里<类似与以下关键字>
    //DemoJpa findByFirstName(String firstName);
    DemoJpa findByFirstNameIs(String firstName);

    //在Age数值age到age2之间的数据
    List<DemoJpa> findByAgeBetween(Integer age, Integer age2);

    //小于指定age数值之间的数据
    List<DemoJpa> findByAgeLessThan(Integer age);

    //小于等于指定age数值的数据
    List<DemoJpa> findByAgeLessThanEqual(Integer age);

    //大于指定age数值之间的数据
    List<DemoJpa> findByAgeGreaterThan(Integer age);

    //大于或等于指定age数值之间的数据
    List<DemoJpa> findByAgeGreaterThanEqual(Integer age);

    //在指定age数值之前的数据类似关键字<LessThan>
    List<DemoJpa> findByAgeAfter(Integer age);

    //在指定age数值之后的数据类似关键字<GreaterThan>
    List<DemoJpa>  findByAgeBefore(Integer age);

    //返回age字段为空的数据
    List<DemoJpa> findByAgeIsNull();

    //返回age字段不为空的数据
    List<DemoJpa> findByAgeNotNull();

    /**
     * 该关键字我一度以为是类似数据库的模糊查询,
     * 但是我去官方文档看到它里面并没有通配符。
     * 所以我觉得它类似
     * DemoJpa findByFirstName(String firstName);
     * @see https://docs.spring.io/spring-data/jpa/docs/2.1.5.RELEASE/reference/html/#jpa.repositories
     */
    DemoJpa findByFirstNameLike(String firstName);

    //同上
    List<DemoJpa> findByFirstNameNotLike(String firstName);

    //查找数据库中指定类似的名字(如：输入一个名字"M" Jpa会返回多个包含M开头的名字的数据源)<类似数据库模糊查询>
    List<DemoJpa> findByFirstNameStartingWith(String firstName);

    //查找数据库中指定不类似的名字(同上)
    List<DemoJpa> findByFirstNameEndingWith(String firstName);

    //查找包含的指定数据源(这个与以上两个字段不同的地方在与它必须输入完整的数据才可以查询)
    List<DemoJpa> findByFirstNameContaining(String firstName);

    //根据age选取所有的数据源并按照LastName进行升序排序
    List<DemoJpa> findByAgeOrderByLastName(Integer age);

    //返回不是指定age的所有数据
    List<DemoJpa> findByAgeNot(Integer age);

    //查找包含多个指定age返回的数据
    List<DemoJpa> findByAgeIn(List<Integer> age);

}

```
UserEntityDao.java
```java
package com.zja.dao;


import com.zja.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Date: 2019-12-11 17:18
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
//@Repository
public interface UserEntityDao extends JpaRepository<UserEntity,Long>{

    //查询所有对象，排序
    //@Override
    //List<UserEntity> findAll(Sort sort);


    //批量删除
    /*@Override
    void deleteInBatch(Iterable<UserEntity> entities);*/

    //删除所有
    /*@Override
    void deleteAllInBatch();*/

    /**
     * 使用@Query 创建查询
     * 描述：推荐使用这种方法，可以不用管参数的位置
     * 注意：UserEntity是实体类名称，不是表名称，userName是属性名称，不是表字段
     * @param userName 参数：用户名-不重复，具有唯一性
     */
    @Query("select u from UserEntity u where u.userName = :userName")
    UserEntity findUserByUserName(@Param("userName") String userName);

    /**
     * 占位符? 注意参数位置
     * 1表示第一个参数
     * @param age 参数：年龄
     */
    @Query("select u from UserEntity u where u.age = ?1")
    List<UserEntity> findUserByAge(Integer age);

    /**
     * 修改查询
     * 使用 @Query 来执行一个更新操作，用 @Modifying 来将该操作标识为修改查询，最终会生成一个更新的操作，而非查询操作
     * @param userName 用户名
     * @param newUserName 新的用户名
     */
    @Transactional
    @Modifying
    @Query(value="update UserEntity u set u.userName=:newUserName where u.userName like %:userName")
    int findByUpdateUserName(@Param("userName") String userName,@Param("newUserName") String newUserName);

    /**
     * 使用@Query来指定sql原始语句查询，只要设置nativeQuery为true
     * 注意：userentity为表明，username表字段
     * @param userName
     */
    @Query(value = "select * from userentity u where u.username like %?1",nativeQuery = true)
    List<UserEntity> findUserByLikeUserName(String userName);

}

```


### 3、单元测试(jpa使用)

测试类
DemoJpaRepositoriesTest.java
```java
import com.zja.dao.DemoJpaRepositories;
import com.zja.entity.DemoJpa;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2019-12-12 17:18
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoJpaRepositoriesTest {

    @Autowired
    private DemoJpaRepositories repositories;

    @Test
    public void saveAll() {

        List<DemoJpa> demoJpas = new ArrayList<>();

        for (int i=1;i<10;i++){
            DemoJpa demoJpa = new DemoJpa();
            demoJpa.setId(i);
            demoJpa.setAge(14+i);
            demoJpa.setEmail("126@qq.com");
            demoJpa.setFirstName("May"+i);
            demoJpa.setLastName("Eden"+i);
            demoJpas.add(demoJpa);
        }

        List<DemoJpa> jpas = this.repositories.saveAll(demoJpas);
        System.out.println(jpas);
    }

    @Test
    public void findByFirstNameAndLastName() {
        DemoJpa demoJpa = repositories.findByFirstNameAndLastName("May1", "Eden1");
        System.out.println(demoJpa);
        Assert.assertEquals(demoJpa.getFirstName(),"May1");
    }

    @Test
    public void findByLastNameOrFirstName() {
        DemoJpa demoJpa = repositories.findByLastNameOrFirstName("Eden1", "May1");
        System.out.println(demoJpa);
        //Assert.assertNotEquals(demoJpa.getLastName(),"Eden1");
    }

    @Test
    public void findByFirstNameIs() {
        DemoJpa demoJpa = repositories.findByFirstNameIs("May2");
        System.out.println(demoJpa);
        //Assert.assertNull(demoJpa);
    }

    @Test
    public void findByAgeBetween() {
        List<DemoJpa> demoJpaList = repositories.findByAgeBetween(15, 17);
        System.out.println(demoJpaList);
        Assert.assertEquals(3,demoJpaList.size());
    }

    @Test
    public void findByAgeLessThan() {
        List<DemoJpa> demoJpaList = repositories.findByAgeLessThan(17);
        System.out.println(demoJpaList);
        Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeLessThanEqual() {
        List<DemoJpa> demoJpaList = repositories.findByAgeLessThanEqual(17);
        System.out.println(demoJpaList);
        Assert.assertEquals(3,demoJpaList.size());
    }

    @Test
    public void findByAgeGreaterThan() {
        List<DemoJpa> demoJpaList = repositories.findByAgeGreaterThan(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeGreaterThanEqual() {
        List<DemoJpa> demoJpaList = repositories.findByAgeGreaterThanEqual(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(3,demoJpaList.size());
    }

    @Test
    public void findByAgeAfter() {
        List<DemoJpa> demoJpaList = repositories.findByAgeAfter(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeBefore() {
        List<DemoJpa> demoJpaList = repositories.findByAgeBefore(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeIsNull() {
        List<DemoJpa> demoJpaList = repositories.findByAgeIsNull();
        System.out.println(demoJpaList);
        //Assert.assertEquals(0,demoJpaList.size());
    }

    @Test
    public void findByAgeNotNull() {
        List<DemoJpa> demoJpaList = repositories.findByAgeNotNull();
        System.out.println(demoJpaList);
        //Assert.assertEquals(5,demoJpaList.size());
    }

    @Test
    public void findByFirstNameLike() {
        DemoJpa demoJpa = repositories.findByFirstNameLike("May");
        System.out.println(demoJpa);
        //Assert.assertNotNull(demoJpa);
    }

    @Test
    public void findByFirstNameNotLike() {

    }

    @Test
    public void findByFirstNameStartingWith() {
        List<DemoJpa> demoJpaList = repositories.findByFirstNameStartingWith("May");
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByFirstNameEndingWith() {
        List<DemoJpa> demoJpaList = repositories.findByFirstNameEndingWith("May");
        System.out.println(demoJpaList);
        //Assert.assertEquals(0,demoJpaList.size());
    }

    @Test
    public void findByFirstNameContaining() {
        List<DemoJpa> demoJpaList = repositories.findByFirstNameContaining("May");
        System.out.println(demoJpaList);
        //Assert.assertEquals(0,demoJpaList.size());
    }

    @Test
    public void findByAgeOrderByLastName() {
        List<DemoJpa> demoJpaList = repositories.findByAgeOrderByLastName(18);
        for (DemoJpa demoJpaL : demoJpaList){
            System.out.println("数据结果"+demoJpaL.toString());
            //log.info("数据结果"+demoJpaL.toString());
        }
    }

    @Test
    public void findByAgeNot() {
        List<DemoJpa> demoJpaList = repositories.findByAgeNot(20);
        System.out.println(demoJpaList);
        //Assert.assertEquals(5,demoJpaList.size());
    }

    @Test
    public void findByAgeIn() {
        List<DemoJpa> demoJpaList = repositories.findByAgeIn(Arrays.asList(15, 16));
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }
}

```

UserEntityDaoTest.java
```java
import com.zja.dao.UserEntityDao;
import com.zja.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Date: 2019-12-12 17:49
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class UserEntityDaoTest {

    @Autowired
    private UserEntityDao userEntityDao;

    //批量保存数据
    @Test
    public void saveAll(){
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
        System.out.println(jpas);
    }

    //查询全部数据
    @Test
    public void findAll(){
        List<UserEntity> userEntityList = this.userEntityDao.findAll();
        System.out.println(userEntityList);
    }

    //按实体类属性更新(修改)
    @Test
    public void findByUpdateUserName(){
        int updateResult = this.userEntityDao.findByUpdateUserName("李四2","张三");
        System.out.println(updateResult);
        //按实体类属性用户名like模糊查询
        UserEntity byUserName = this.userEntityDao.findUserByUserName("张三");
        System.out.println(byUserName);
    }

    //按实体类属性age查询
    @Test
    public void findUserByAge(){
        List<UserEntity> userEntityList = this.userEntityDao.findUserByAge(15);
        System.out.println(userEntityList);
    }

    //like模糊查询
    @Test
    public void findUserByLikeUserName(){
        List<UserEntity> userEntityList = this.userEntityDao.findUserByLikeUserName("李四%");
        System.out.println(userEntityList);
    }

    //排序并分页
    @Test
    public void pageable(){
        Sort sort =new Sort(Sort.Direction.DESC,"age");
        Pageable pageable = PageRequest.of(2,3,sort);
        Page<UserEntity> entityPage = userEntityDao.findAll(pageable);
        List<UserEntity> userEntityList = entityPage.getContent();
        System.out.println(userEntityList);
        System.out.println("每页几条数据："+entityPage.getSize());
        System.out.println("当前页："+entityPage.getNumber());
        System.out.println("排序方式："+entityPage.getSort());
        System.out.println("总数据条数："+entityPage.getTotalElements());
        System.out.println("总页数"+entityPage.getTotalPages());
    }

    //排序不分页
    @Test
    public void sort(){
        Sort sort =new Sort(Sort.Direction.ASC,"age");
        List<UserEntity> userEntityList = userEntityDao.findAll(sort);
        System.out.println(userEntityList);
    }

    //批量删除数据
    @Test
    public void deleteInBatch(){
        List<UserEntity> userEntities = new ArrayList<>();

        for (int i=1;i<5;i++){

            UserEntity userEntity = new UserEntity();
            userEntity.setId(i+0L);
            userEntity.setUserName("李四"+i);
            userEntity.setAge(14+i);
            userEntity.setCreateTime(new Date());
            userEntity.setUpdateTime(new Date());

            userEntities.add(userEntity);
        }
        //实际按id批量删除
        userEntityDao.deleteInBatch(userEntities);
        //查询删除后的数据
        List<UserEntity> userEntityList = userEntityDao.findAll();
        System.out.println(userEntityList);
    }

    //删除全部数据
    @Test
    public void deleteAllInBatch(){
        userEntityDao.deleteAllInBatch();
        //查询删除后的数据
        List<UserEntity> userEntityList = userEntityDao.findAll();
        System.out.println(userEntityList);
    }
}

```

### 4、项目完整pom
pom
```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.zja</groupId>
        <artifactId>spring5x</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <groupId>com.zja</groupId>
    <artifactId>spring5x-data-jpa</artifactId>
    <packaging>war</packaging>

    <name>spring5x-data-jpa</name>

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

        <!--junit5-->
        <junit5.version>5.1.0</junit5.version>
        <junit5-platform.version>1.1.0</junit5-platform.version>
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
            <artifactId>spring-oxm</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring.version}</version>
        </dependency>
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
        <!-- spring web 整合 spring MVC所需jar：webmvc -->
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

        <!--jackson  默认采用 Jackson 将对象进行序列化和反序列化-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.10.1</version>
            <exclusions>
                <exclusion>
                    <artifactId>jackson-annotations</artifactId>
                    <groupId>com.fasterxml.jackson.core</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>jackson-core</artifactId>
                    <groupId>com.fasterxml.jackson.core</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.4</version>
        </dependency>

        <!--测试相关依赖-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!-- junit5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit5.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-runner</artifactId>
            <version>${junit5-platform.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-console-standalone</artifactId>
            <version>${junit5-platform.version}</version>
            <scope>test</scope>
        </dependency>

        <!--日志，修复日志-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-nop</artifactId>
            <version>1.7.28</version>
        </dependency>

        <!-- 使用lombok实现JavaBean的get、set、toString、hashCode、equals等方法的自动生成  -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.2</version>
            <scope>provided</scope>
            <optional>true</optional>
        </dependency>

        <!--spring5.x 集成swagger2-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
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
            <version>2.9.2</version>
        </dependency>
        <!--增加两个配置解决swagger2 2.9.x 报错java.lang.NumberFormatException: For input string: ""-->
        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-annotations</artifactId>
            <version>1.5.22</version>
        </dependency>
        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-models</artifactId>
            <version>1.5.22</version>
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

        <!--druid 数据源连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.20</version>
        </dependency>
        <!--c3p0 数据源连接池-->
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.2</version>
        </dependency>
        <!--hibernate-c3p0 数据源连接池-->
        <!--<dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-c3p0</artifactId>
            <version>5.3.10.Final</version>
        </dependency>-->

        <!-- hibernate 框架 -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>5.4.4.Final</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>5.4.4.Final</version>
        </dependency>
        <!--hibernate或jpa所需依赖,不然会找不到session工厂或transaction -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!--spring-data-jpa-->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-jpa</artifactId>
            <version>2.1.10.RELEASE</version>
            <exclusions>
                <exclusion>
                    <artifactId>spring-beans</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-aop</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-context</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-core</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-jdbc</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-tx</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>slf4j-api</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>

    </dependencies>

    <build>
        <finalName>spring5x-data-jpa</finalName>
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


### 5、项目的github和简书博客地址
**github:**
- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)

**博客:**
- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts
