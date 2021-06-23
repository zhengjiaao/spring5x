
# spring5x-hibernate5-senior

[toc]

spring5x-hibernate5-senior 此模块是从spring5x-hibernate5-base 基础模块扩展过来的
spring5x-hibernate5-base模块是一个hibernate5基础架构，可参考[spring5x-hibernate5-base 基础模块]()

## 搭建项目

**基于spring5x-hibernate5-base 基础模块功能：**

- 1、hibernate5 依赖及xml方式配置
- 2、支持c3p0/druid连接池和mysql/oracle数据库
- 3、HibernateTemplate 增删改查操作



### 1、hibernate5 依赖及xml方式配置

****

pom.xml 依赖

```xml
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
        <!--hibernate-c3p0 数据源连接池-->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-c3p0</artifactId>
            <version>5.3.10.Final</version>
        </dependency>

        <!-- spring的整合hibernate所需的jar：orm,不然会找不到session工厂 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!--apo 解决问题:Failed to introspect Class [org.springframework.aop.aspectj-->
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.2</version>
        </dependency>

        <!--Hibernate-->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>5.3.7.Final</version>
            <exclusions>
                <exclusion>
                    <artifactId>classmate</artifactId>
                    <groupId>com.fasterxml</groupId>
                </exclusion>
            </exclusions>
        </dependency>

```

spring-hibernate5-1.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 数据源配置资源引入 -->
    <!--druid.xml 配置-->
    <import resource="classpath:META-INF/spring/datasource/spring-druid.xml"/>
    <!--c3p0.xml 配置-->
    <!--<import resource="classpath:META-INF/spring/datasource/spring-c3p0.xml"/>-->

    <!-- Hibernate 配置  BEGIN -->

    <!-- **************配置SessionFactory工厂*************** -->

    <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <!--配置数据源-->
        <property name="dataSource" ref="dataSource"/>

        <!-- 自动扫描注解：实体类，使用注解@Entity，@Table，@Id等等，不需要hbm.xml -->
<!--        <property name="packagesToScan">
            <list>
                <value>com.zja.entity</value>
            </list>
        </property>-->

        <!--自动扫描配置：*.hbm.xml，可不使用packagesToScan-->
        <!--<property name="mappingLocations" value="classpath*:com/zja/entity/*.hbm.xml"/>-->

        <!--自动扫描配置：*.hbm.xml,可不使用packagesToScan-->
        <property name="mappingLocations">
            <list>
                <!--添加扫描*.hbm.xml文件的路径-->
                <value>classpath*:/hbm/*.hbm.xml</value>
                <!--<value>classpath*:com/zja/entity/*.hbm.xml</value>-->
            </list>
        </property>

        <!-- 配置 hibernate-1.cfg.xml 中的信息 -->
        <property name="hibernateProperties">
            <props>
                <!--数据库方言:Hibernate-5.2.10 之后使用的方言需要升级-->
                <!--mysql新版本方言:创建表时,引擎engine=MyISAM，优点：查询很快，缺点：不支持事务处理和外键约束-->
                <!--<prop key="hibernate.dialect">org.hibernate.dialect.MySQL5Dialect</prop>-->
                <!--mysql新版本方言:创建表时,引擎engine=InnoDB，优点：支持事务处理和外键约束，缺点：查询很慢，(推荐使用)-->
                <!--<prop key="hibernate.dialect">org.hibernate.dialect.MySQL57Dialect</prop>-->

                <!--oracle新版本方言: 支持版本10g和11g-->
                <prop key="hibernate.dialect">org.hibernate.dialect.Oracle10gDialect</prop>

                <!--default_schema：oracle设置用户名:duke，mysql设置数据库名称:test。不推荐使用：默认自动识别-->
                <!--<prop key="hibernate.default_schema">duke</prop>-->
                <!--<prop key="hibernate.default_schema">test</prop>-->

                <!--create每次加载hibernate，重新创建数据库表结构，这就是导致数据库表数据丢失的原因
                    create-drop加载hibernate时删除表再创建表，退出时删除表结构
                    update加载hibernate自动更新数据库结构,建议禁用update，问题太多(比如第二次运行项目，bug会再次创建表，这时表已存在，报错)
                    validate加载hibernate时，验证创建数据库表结构,只会和数据库中的表进行比较，不会创建新表，但是会插入新值
                    none不是用此功能(推荐使用) -->
                <prop key="hibernate.hbm2ddl.auto">none</prop>
                <!--打印sql语句-->
                <prop key="hibernate.show_sql">true</prop>
                <!--sql格式化-->
                <prop key="hibernate.format_sql">true</prop>
                <!--禁用 jdbc元数据默认值-->
                <prop key="hibernate.temp.use_jdbc_metadata_defaults">false</prop>
            </props>
        </property>
    </bean>


    <!-- **************配置事务管理器*************** -->

    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>

    <!-- 注解方式(配置事物)：通过@Transactional启用事物管理，xml方式的配置会覆盖注解配置 -->
    <!--<tx:annotation-driven transaction-manager="transactionManager" />-->

    <!-- XML方式(配置事物):配置事务特性，配置add，delete，update开始的方法，事务传播特性为required -->
    <!-- 配置事务通知属性 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!-- 定义事务传播属性 -->
        <tx:attributes>
            <tx:method name="save*" propagation="REQUIRED"/>
            <tx:method name="merge*" propagation="REQUIRED"/>
            <tx:method name="insert*" propagation="REQUIRED"/>
            <tx:method name="add*" propagation="REQUIRED"/>
            <tx:method name="set*" propagation="REQUIRED"/>
            <tx:method name="new*" propagation="REQUIRED"/>
            <tx:method name="update*" propagation="REQUIRED"/>
            <tx:method name="edit*" propagation="REQUIRED"/>
            <tx:method name="remove*" propagation="REQUIRED"/>
            <tx:method name="delete*" propagation="REQUIRED"/>
            <tx:method name="execute*" propagation="REQUIRED"/>
            <tx:method name="change*" propagation="REQUIRED"/>
            <tx:method name="get*" propagation="REQUIRED" read-only="true"/>
            <tx:method name="find*" propagation="REQUIRED" read-only="true"/>
            <tx:method name="load*" propagation="REQUIRED" read-only="true"/>
            <tx:method name="*" propagation="REQUIRED" read-only="true"/>
        </tx:attributes>
    </tx:advice>
    <!-- 配置那些类的方法进行事务管理，当前com.sy.crm.service包中的子包，类中所有方法需要，还需要参考tx:advice的设置 -->
    <!--dao是原子的数据库操作
        service层可以把多个原子的数据库操作组合
        为了让一个事务里面可以进行多个数据库操作，因此把事务放在service层-->
    <aop:config>
        <aop:pointcut id="allManagerMethod" expression="execution(* com.zja.service.*.*(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="allManagerMethod"/>
    </aop:config>


    <!-- **************配置hibernateTemplate 操作模板*************** -->

    <!-- 配置HibernateTemplate模板操作类,Spring调用 Hibernate 的持久化操作,HibernateTemplate是线程安全的  -->
    <bean id="hibernateTemplate" class="org.springframework.orm.hibernate5.HibernateTemplate">
        <property name="sessionFactory" ref="sessionFactory"/>
        <!-- spring与hibernate整合后默认必须在事务下对数据库执行写操作，如果将checkWriteOperations设置为false则没有事务也可写数据库，这样是为了单独测试dao方便 -->
        <!--上面已经配置了事务，注释掉下方代码-->
        <!--<property name="checkWriteOperations" value="false"/>-->
    </bean>
    <!-- Hibernate 配置  END -->

</beans>

```
### 2、支持c3p0/druid连接池和mysql/oracle数据库

jdbc.properties
```properties
# mysql 数据库配置:
#新的驱动driverClassName
mysql.jdbc.driverClassName=com.mysql.cj.jdbc.Driver
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

spring-druid.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <!--指定jdbc配置文件的位置-->
    <context:property-placeholder location="classpath:properties/jdbc.properties" ignore-unresolvable="true"/>

    <!--配置 druid 数据源 关于更多的配置项 可以参考官方文档 <a href="https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8" > -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">

        <!-- 基本属性 url、user、password ，driverClassName默认自动识别-->
        <!--配置mysql -->
        <!--<property name="url" value="${mysql.jdbc.url}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>-->

        <!--配置oracle -->
        <property name="url" value="${oracle.jdbc.url}"/>
        <property name="username" value="${oracle.jdbc.username}"/>
        <property name="password" value="${oracle.jdbc.password}"/>
        <property name="validationQuery" value="${oracle.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${oracle.jdbc.driverClassName}"/>

        <!--validationQuery属性说明：用来检测连接是否有效的 sql，要求是一个查询语句，常用 select 'x'。
            但是在 oracle 数据库下需要写成 select 'x' from dual 不然实例化数据源的时候就会失败,
            这是由于 oracle 和 mysql 语法间的差异造成的-->

        <!--*****druid 公共属性配置*****-->
        <!-- 初始化连接大小 -->
        <property name="initialSize" value="10"/>
        <!-- 连接池最小空闲 -->
        <property name="minIdle" value="10"/>
        <!-- 连接池最大使用连接数量 -->
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

        <!-- 配置连接池中，创建连接时，采用异步|同步方式来进行创建，默认是：异步 -->
        <property name="asyncInit" value="true" />

        <!-- 配置监控统计拦截的 filters Druid 连接池的监控信息主要是通过 StatFilter 采集的，
        采集的信息非常全面，包括 SQL 执行、并发、慢查、执行时间区间分布等-->
        <!-- 配置额外的扩展插件:stat 代表开启监控程序，wall 代表开启防SQL注入功能 -->
        <property name="filters" value="stat,wall"/>
    </bean>

</beans>

```

spring-c3p0.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:properties/jdbc.properties" ignore-unresolvable="true"/>

    <!-- 配置 C3P0 数据源 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">

        <!--配置mysql -->
        <property name="driverClass" value="${mysql.jdbc.driverClassName}" />
        <property name="jdbcUrl" value="${mysql.jdbc.url}" />
        <property name="user" value="${mysql.jdbc.username}" />
        <property name="password" value="${mysql.jdbc.password}" />

        <!--配置oracle -->
        <!--<property name="driverClass" value="${oracle.jdbc.driverClassName}" />
        <property name="jdbcUrl" value="${oracle.jdbc.url}" />
        <property name="user" value="${oracle.jdbc.username}" />
        <property name="password" value="${oracle.jdbc.password}" />-->

        <!--c3p0公共属性配置-->
        <!-- 数据库连接池中的最大的数据库连接数,建议在开发环境中设置小一点,够用即可 -->
        <property name="maxPoolSize" value="25"/>
        <!-- 数据库连接池中的最小的数据库连接数 -->
        <property name="minPoolSize" value="5"/>
        <!-- 如果池中数据连接不够时一次增长多少个 -->
        <property name="acquireIncrement" value="5"/>
        <!-- 初始化数据库连接池时连接的数量 -->
        <property name="initialPoolSize" value="20"/>

    </bean>

</beans>

```

### 3、HibernateTemplate 增删改查操作

UserEntity.java
```java
@Data
@Entity
@Table(name = "userentity")
@ApiModel("用户信息实体类")
public class UserEntity implements Serializable {

    @ApiModelProperty(value = "默认:mysql自增,oracle序列")
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @ApiModelProperty("用户名")
    @Basic(fetch =FetchType.EAGER,optional = false) //急加载，属性是否允许为null
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

resources/hbm/UserEntity.hbm.xml
```xml
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
    <class name="com.zja.entity.UserEntity" table="userentity">
        <id name="id" column="id" type="int">
            <!--<column name="id" sql-type="int"/>-->
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment" />
            <!--根据数据库自动选择identity、hilo、sequence其中一种，例如MySQL使用identity，Oracle使用sequence(默认序列查找hibernate_sequence序列)，使用自定义序列时需加入参数sequence_name-->
            <!--<generator class="native">
                <param name="sequence_name">SEQ_MY_HIBERNATE</param>
            </generator>-->
            <!--需要数据库支持sequence,例如oralce、PostgerSQL等，不支持 mysql(可以使用identity)-->
            <!--<generator class="sequence">
                &lt;!&ndash;SEQ_MY_HIBERNATE 是自定义创建的序列，指定sequence的名称&ndash;&gt;
                <param name="sequence_name">SEQ_MY_HIBERNATE</param>
            </generator>-->
        </id>
        <property name="userName">
            <!--不可为null-->
            <column name="username" sql-type="varchar(225)" length="225" not-null="true"/>
        </property>
        <property name="age">
            <!--可为null-->
            <column name="age" sql-type="int" not-null="false"/>
        </property>
        <property name="createTime">
            <column name="createtime" sql-type="date" not-null="false"/>
        </property>
        <property name="updateTime">
            <column name="updatetime" sql-type="timestamp" not-null="false"/>
        </property>
    </class>
</hibernate-mapping>

```

**HibernateTemplate 使用**
```java
package com.zja.service.Impl;

import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/19
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource
    private HibernateTemplate hibernateTemplate;


    /*********** save/merge存数据 *************/

    /**
     * save保存-成功
     *
     * @param userEntity
     */
    @Override
    public Object saveEntity(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.save(userEntity);
    }

    /**
     * save保存-成功
     *
     * @param userEntity
     */
    @Override
    public Object saveEntity2(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.save("userEntity", userEntity);
    }

    /**
     * merge保存-成功 :存在则更新，不存在则保存
     * 使用merge的时候，执行完成，我们提供的对象A还是脱管状态，hibernate或者new了一个B，或者检索到
     * 一个持久对象B，并把我们提供的对象A的所有的值拷贝到这个B，执行完成后B是持久状态，而我们提供的A还是托管状态
     *
     * @param userEntity 实体类
     */
    @Override
    public UserEntity mergeEntity(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.merge(userEntity);
    }

    /**
     * merge保存-成功 :存在则更新，不存在则保存
     *
     * @param userEntity 实体类
     */
    @Override
    public UserEntity mergeEntity2(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.merge("userEntity", userEntity);
    }


    /*********** get/load存取单条数据 *************/
    //get和load的根本区别:
    // hibernate对于load方法认为该数据在数据库中一定存在，可以放心的使用代理来延迟加载，如果在 使用过程中发现了问题，就抛异常；
    // 对于get方法，hibernate一定要获取到真实的数据，否则返回null

    /**
     * 根据id获取用户信息-成功
     * 查询数据不存在，返回null,推荐使用, 不存在延迟加载问题，不采用lazy机制的
     *
     * @param id
     */
    @Override
    public UserEntity getEntity(Integer id) {
        return hibernateTemplate.get(UserEntity.class, id);
    }

    /**
     * load查询单条数据，查询数据不存在，报异常，不推荐使用,存在延迟加载问题-失败
     *
     * @param id
     */
    @Override
    public UserEntity loadEntity(Integer id) {
        return hibernateTemplate.load(UserEntity.class, id);
    }

    /**
     * loadAll查询所有用户信息-成功
     *
     * @param
     */
    @Override
    public List<UserEntity> getListEntity() {
        return hibernateTemplate.loadAll(UserEntity.class);
    }

    /**
     * 根据id查询-失败
     *
     * @param id
     */
    @Override
    public UserEntity getEntityName(Integer id) {
        Object userEntity = hibernateTemplate.get("userEntity", id);
        System.out.println("userEntity: " + userEntity);
        return (UserEntity) hibernateTemplate.get("userEntity", id);
    }

    /**
     * 分页查询-不推荐用-成功
     */
    @Override
    public List<UserEntity> getPageByConditions() {
        //hibernateTemplate单例,分页会有问题,执行一次分页后,其它的所有查询都是分页后效果
        hibernateTemplate.setMaxResults(5);
        List<UserEntity> loadAll = hibernateTemplate.loadAll(UserEntity.class);
        hibernateTemplate.setMaxResults(0);
        return loadAll;
    }

    /*********** 更新 *************/

    /**
     * 更新-成功
     *
     * @param userEntity
     */
    @Override
    public void updateEntity(UserEntity userEntity) {
        hibernateTemplate.update(userEntity);
    }

    /**
     * 保存或更新：id不存在则保存，存在则更新-成功
     *
     * @param userEntity
     */
    @Override
    public void saveOrUpdate(UserEntity userEntity) {
        hibernateTemplate.saveOrUpdate(userEntity);
    }

    /*********** 删除 *************/

    /**
     * 删除单条数据-成功
     *
     * @param id
     */
    @Override
    public void deleteByEntity(Integer id) {
        hibernateTemplate.delete(getEntity(id));
    }

    /**
     * 删除全部数据-成功
     *
     * @param
     */
    @Override
    public void deleteAll() {
        List<UserEntity> userEntityList = findByCriteria();
        hibernateTemplate.deleteAll(userEntityList);
    }


    /*********** hibernateTemplate执行原生sql脚本 *************/

    /**
     * HiberateTemplate提供的方法不能满足要求时才使用execute方法 执行sql -成功
     */
    @Override
    public Object execute() {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                //创建空表mysql_ok
                session.createSQLQuery(
                        "DROP TABLE IF EXISTS `mysql_ok`;")
                        .executeUpdate();
                int i = session.createSQLQuery(
                        "CREATE TABLE `mysql_ok` (`ok`  varchar(3) NOT NULL ,PRIMARY KEY (`ok`));")
                        .executeUpdate();
                System.out.println("成功创建空表mysql_ok ：i=" + i);
                return i;
            }
        });
        return o;
    }

    /**
     * 保存实体类-成功
     * @param userEntity
     */
    @Override
    public Object executeSaveUserEntity(UserEntity userEntity) {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                Serializable save = session.save(userEntity);
                System.out.println("id=" + save);
                return save;
            }
        });
        return o;
    }



    //1. HQL查询
    /*********** HQL查询 面向对象查询数据 *************/

    /**
     * 通过Hql执行获取所有用户
     */
    @Override
    public Object getAllUserEntityByHql() {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                // 这里UserEntity是对象名，不是数据库表名
                String hql ="from UserEntity";
                Query<UserEntity> query = session.createQuery(hql);
                List<UserEntity> userEntityList = query.list();
                return userEntityList;
            }
        });
        return o;
    }

    /**
     * 通过Hql执行获取唯一的用户
     */
    @Override
    public Object getUserEntityByHql(Integer id) {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                // 这里UserEntity是对象名，不是数据库表名,id也不是数据库字段名称
                String hql ="from UserEntity where id ="+id;
                //推荐使用，易读
                Query<UserEntity> query = session.createQuery(hql);
                // 知道返回结果是唯一的，使用uniqueResult
                UserEntity userEntity = query.uniqueResult();
                return userEntity;
            }
        });
        return o;
    }

    @Override
    public Object getNameByHQL() {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                // 这里UserEntity是对象名，不是数据库表名,username也不是数据库字段名称
                String hql ="select userName from UserEntity";
                Query<UserEntity> query = session.createQuery(hql);
                //集合中直接存放着name属性，不是对象
                List<UserEntity> userEntityList = query.list();
                //结果： ["小明","小刘","小王","明月","秦时","阿里"]
                return userEntityList;
            }
        });
        return o;
    }


    //2.原生SQL查询
    /*********** 原生SQL查询数据 *************/

    /**
     * 通过Sql执行获取所有用户
     */
    @Override
    public Object executeGetUserEntityBySql() {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                // 这里UserEntity是对象名，不是数据库表名
                String sql ="select * from userentity";
                // 创建一个接收结果集的SQLQuery对象
                NativeQuery sqlQuery = session.createSQLQuery(sql);
                // 通过addEntity,来绑定到实体
                List<UserEntity> userEntityList = sqlQuery.addEntity(UserEntity.class).list();

                return userEntityList;
            }
        });
        return o;
    }


    /*********** findByExample查询数据 *************/

    /**
     * 根据条件查询-成功
     */
    @Override
    public List<UserEntity> findByExample() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("小刘");
        userEntity.setAge(20);
        //必须 符合的条件但是这 "username=小刘 and age=20" 两个条件时并列的（象当于sql中的and)
        return hibernateTemplate.findByExample(userEntity);
    }

    /**
     * 根据调条件查询并分页-成功
     *
     * @param firstResult 页码 从0开始
     * @param maxResults  每页显示多少条数据，等于0或负数则查询所有
     */
    @Override
    public List<UserEntity> findByExample(int firstResult, int maxResults) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("小刘");
        userEntity.setAge(20);
        return hibernateTemplate.findByExample(userEntity, firstResult, maxResults);
    }

    //3.QBC（Query By Criteria）命名查询 面向对象查询
    /*********** findByExample查询数据 *************/

    /**
     * 查询所有数据 -成功
     */
    @Override
    public List<UserEntity> findByCriteria() {
        //DetachedCriteria：离线条件查询对象，该对象的创建不需要session对象，替代了Criteria查询
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria);
    }

    /**
     * 分页查询-成功
     *
     * @param firstResult 页码 从0开始
     * @param maxResults  每页显示多少条数据，等于0或负数则查询所有
     */
    @Override
    public List<UserEntity> findPageByCriteria(int firstResult, int maxResults) {
        //firstResult 第几页，maxResults每页显示多少条
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }

    /**
     * 根据属性名称和属性值查询-成功
     *
     * @param propertyName  类的属性名称
     * @param propertyValue 属性值
     * @param firstResult   页码 从0开始
     * @param maxResults    每页显示多少条数据，等于0或负数则查询所有
     */
    @Override
    public List<UserEntity> findByCriteria(String propertyName, Object propertyValue, int firstResult, int maxResults) {
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        //根据属性名称和属性的value值查询
        criteria.add(Restrictions.eq(propertyName, propertyValue));
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }

    /**
     * 模糊查询按年龄区间和大小排序-成功
     *
     * @param low  低
     * @param high 高  low --> high 从低到高，顺序一定要正确，否则查询会有问题，或查询不到数据
     */
    @Override
    public List<UserEntity> findByCriteria(int low, int high) {
        //
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        //根据属性名称和属性的value值查询
        criteria.add(Restrictions.like("userName", "小%"));
        criteria.add(Restrictions.between("age", low, high));
        //根据属性名称进行排序
        criteria.addOrder(Order.desc("age"));
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria);
    }


}

```

## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)


## 博客地址:

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts

