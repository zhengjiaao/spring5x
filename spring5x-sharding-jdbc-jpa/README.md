# spring5x-sharding-jdbc-jpa
[toc]

spring5x-sharding-jdbc-jpa此模块是从spring5x-data-jpa模块扩展过来的
spring5x-data-jpa 模块是一个非常干净的spring5.x+springMVC+jpa架构
如果没有搭建spring5x-data-jpa模块，请参考 [spring5x-data-jpa搭建](https://www.jianshu.com/p/2737d5e3a960)


## 搭建项目

**基于spring5x-data-jpa 基础模块 新增功能：**

- 1、spring5.x集成sharding jdbc依赖
- 2、sharding-jdbc xml配置
- 3、单元测试(sharding jdbc 分库分表测试)
- 4、项目的github和博客地址

**项目架构：spring5.x+jpa+sharding jdbc+druid+mysql**

说明：sharding jdbc 作用分库分表，具体百度或参考官网： https://shardingsphere.apache.org/document/current/cn/overview/
 
这里只讲spring项目以xml方式如何配置和使用sharding jdbc。

### 1、spring5.x集成sharding jdbc依赖
- sharding jdbc依赖
```xml
        <!-- sharding jdbc 分库分表-->
        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-core</artifactId>
            <version>3.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-namespace</artifactId>
            <version>4.0.0-RC2</version>
        </dependency>
```
### 2、sharding-jdbc xml配置

jdbc.properties 注意，要先创建数据库，不用建表，jpa自动创建表。
```properties
# mysql 数据库公共配置:
mysql.jdbc.driverClassName=com.mysql.cj.jdbc.Driver
#mysql.jdbc.url=jdbc:mysql://127.0.0.1:3306/test?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
mysql.jdbc.username=root
mysql.jdbc.password=123456
mysql.jdbc.validationQuery=select 'x'

#自定义算法策略 分库分表
#自定义分表算法-同库：只分表，不分库
mysql.jdbc.url9=jdbc:mysql://127.0.0.1:3306/custom_tb?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false

#自定义分库算法-只分库，不分表
mysql.jdbc.url10=jdbc:mysql://127.0.0.1:3306/custom_ds_0?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
mysql.jdbc.url11=jdbc:mysql://127.0.0.1:3306/custom_ds_1?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
mysql.jdbc.url12=jdbc:mysql://127.0.0.1:3306/globalDataSource?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false

#自定义分库分表算法-分库分表
mysql.jdbc.url13=jdbc:mysql://127.0.0.1:3306/custom_ds_tb_0?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
mysql.jdbc.url14=jdbc:mysql://127.0.0.1:3306/custom_ds_tb_1?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false

```
druid-common.xml 提取druid数据源公共配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:properties/jdbc.properties" ignore-unresolvable="true"/>

    <!-- druid数据源公共配置 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">

        <!--配置mysql-->
        <!--<property name="url" value="${mysql.jdbc.url}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>-->


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
        <!--<property name="filters" value="stat,wall"/>-->
        <property name="filters" value="stat"/>
    </bean>
</beans>
```

custom-sharding-database.xml 分库:只分库，不分表配置
注意：内部使用的算法，请参考项目代码，这里不贴出来了，下方有github项目地址
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bean="http://www.springframework.org/schema/util"
       xmlns:sharding="http://shardingsphere.apache.org/schema/shardingsphere/sharding"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd http://shardingsphere.apache.org/schema/shardingsphere/sharding http://shardingsphere.apache.org/schema/shardingsphere/sharding/sharding.xsd">

    <!--druid公共配置-->
    <import resource="classpath:META-INF/spring/druid-common.xml"/>

    <bean id="custom_ds_0" parent="dataSource" init-method="init" destroy-method="close">
        <!--配置mysql -->
        <property name="url" value="${mysql.jdbc.url10}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>
    </bean>
    <bean id="custom_ds_1" parent="dataSource" init-method="init" destroy-method="close">
        <property name="url" value="${mysql.jdbc.url11}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>
    </bean>
    <bean id="globalDataSource" parent="dataSource" init-method="init" destroy-method="close">
        <property name="url" value="${mysql.jdbc.url12}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>
    </bean>

    <!--############标准分片配置-分库：自定义分库策略#############-->

    <!-- 分库策略: 尽量使用sharding:standard-strategy(扩展性强),而不是inline-stragegy-->
    <!--<sharding:inline-strategy id="databaseStrategy" sharding-column="user_id" algorithm-expression="custom_ds_${user_id % 2}" />-->

    <!-- 分库算法：精确分片算法和范围分片算法-->
    <bean id="preciseModuloShardingDatabaseAlgorithm" class="com.zja.algorithm.PreciseModuloShardingDatabaseAlgorithm"/>
    <bean id="rangeModuloShardingDatabaseAlgorithm" class="com.zja.algorithm.RangeModuloShardingDatabaseAlgorithm"/>
    <!-- 分表算法：精确分片算法和范围分片算法-->
    <bean id="preciseModuloShardingTableAlgorithm" class="com.zja.algorithm.PreciseModuloShardingTableAlgorithm"/>
    <bean id="rangeModuloShardingTableAlgorithm" class="com.zja.algorithm.RangeModuloShardingTableAlgorithm"/>

    <!--分库策略：precise-algorithm-ref(必选)，range-algorithm-ref(可选)-->
    <sharding:standard-strategy id="databaseStrategy" sharding-column="user_id"
                                precise-algorithm-ref="preciseModuloShardingDatabaseAlgorithm"
                                range-algorithm-ref="rangeModuloShardingDatabaseAlgorithm"/>

    <!--分表策略：precise-algorithm-ref(必选)，range-algorithm-ref(可选)-->
    <sharding:standard-strategy id="tableStrategy" sharding-column="user_id"
                                precise-algorithm-ref="preciseModuloShardingTableAlgorithm"
                                range-algorithm-ref=""/>

    <!--工作id-->
    <bean:properties id="properties">
        <prop key="worker.id">123</prop>
    </bean:properties>

    <!--主键生成器：默认使用雪花算法生成递增趋势id-->
    <sharding:key-generator id="orderKeyGenerator" type="SNOWFLAKE" column="order_id" props-ref="properties"/>
    <sharding:key-generator id="itemKeyGenerator" type="SNOWFLAKE" column="order_item_id" props-ref="properties"/>

    <!--分片数据源-->
    <sharding:data-source id="shardingDataSource">
        <!--数据库名称，默认数据源globalDataSource，不分片的数据表放默认数据库中-->
        <sharding:sharding-rule data-source-names="custom_ds_0,custom_ds_1,globalDataSource" default-data-source-name="globalDataSource">
            <sharding:table-rules>
                <!-- 分库不分表 -->
                <sharding:table-rule logic-table="t_order" database-strategy-ref="databaseStrategy"
                                     key-generator-ref="orderKeyGenerator"/>
                <sharding:table-rule logic-table="t_order_item" database-strategy-ref="databaseStrategy"
                                     key-generator-ref="itemKeyGenerator"/>
            </sharding:table-rules>

            <!-- 绑定表规则列表,表示分库分表的规则相同,这样万一涉及到多个分片的查询,sharding-jdbc就可以确定分库之间不需要不必要的二次关联,所有的查询都应该如此 -->
            <!--绑定表:分片规则一直的主表和子表-->
            <!--t_order表，其分片键是order_id，其子表t_order_item的分片键也是order_id。在规则配置时将两个表配置成绑定关系，就不会在查询时出现笛卡尔积-->
            <sharding:binding-table-rules>
                <!--logic-tables逻辑表名，如果真实表为空，则把逻辑表名作为真实表名-->
                <!-- 配置绑定表(分片规则相同，一般为主表子表的关系)，若不是绑定表不用配置，否则全路由不会走笛卡尔积 -->
                <sharding:binding-table-rule logic-tables="t_order,t_order_item"/>
            </sharding:binding-table-rules>
            <!--广播表:有一些表是没有分片的必要的，比如省份信息表，全国也就30多条数据，这种表在每一个节点上都是一样的，这种表叫做广播表。-->
            <sharding:broadcast-table-rules>
                <!--当插入10条数据，会存到每个库中的t_address表中，每张表都有完整的表数据10条-->
                <sharding:broadcast-table-rule table="t_address"/>
                <!--<sharding:broadcast-table-rule table="t_"/>-->
            </sharding:broadcast-table-rules>
        </sharding:sharding-rule>
    </sharding:data-source>
</beans>

```
custom-sharding-tables.xml 分表：只分表，不分库配置
注意：内部使用的算法，请参考项目代码，这里不贴出来了，下方有github项目地址
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:sharding="http://shardingsphere.apache.org/schema/shardingsphere/sharding"
       xmlns:bean="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://shardingsphere.apache.org/schema/shardingsphere/sharding
                        http://shardingsphere.apache.org/schema/shardingsphere/sharding/sharding.xsd
                        http://www.springframework.org/schema/util
                        http://www.springframework.org/schema/util/spring-util.xsd">

    <!--druid公共配置-->
    <import resource="classpath:META-INF/spring/druid-common.xml"/>

    <bean id="demo_ds" parent="dataSource" init-method="init" destroy-method="close">
        <!--配置mysql -->
        <property name="url" value="${mysql.jdbc.url9}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>
    </bean>

    <!--##########同库分表：只分表，不分库############-->

    <!-- 行表达式算法:分表策略 (注：inline-strategy行表达式的策略不利于数据库和表的横向扩展，不推荐使用) -->
    <!--<sharding:inline-strategy id="orderTableStrategy" sharding-column="order_id" algorithm-expression="t_order_${order_id % 2}" />-->
    <sharding:inline-strategy id="orderItemTableStrategy" sharding-column="order_id" algorithm-expression="t_order_item_${order_id % 2}" />

    <!-- 分表策略  精确分片算法 -->
    <bean id="myPreciseShardingAlgorithm" class="com.zja.myalgorithm.MyPreciseShardingAlgorithm"/>
    <!-- 自定义算法：分表策略 -->
    <sharding:standard-strategy id="orderTableStrategy" sharding-column="order_id"
                                precise-algorithm-ref="myPreciseShardingAlgorithm"/>
    
    <bean:properties id="properties">
        <prop key="worker.id">123</prop>
    </bean:properties>

    <!--雪花算法生成分布式主键-->
    <sharding:key-generator id="orderKeyGenerator" type="SNOWFLAKE" column="order_id" props-ref="properties" />
    <sharding:key-generator id="itemKeyGenerator" type="SNOWFLAKE" column="order_item_id" props-ref="properties" />
    
    <sharding:data-source id="shardingDataSource">
        <sharding:sharding-rule data-source-names="demo_ds">
            <sharding:table-rules>
                <!--分表策略-->
                <sharding:table-rule logic-table="t_order" actual-data-nodes="demo_ds.t_order_${1..2}" table-strategy-ref="orderTableStrategy" key-generator-ref="orderKeyGenerator"/>
                <sharding:table-rule logic-table="t_order_item" actual-data-nodes="demo_ds.t_order_item_${1..2}" table-strategy-ref="orderItemTableStrategy" key-generator-ref="itemKeyGenerator" />
            </sharding:table-rules>
            <sharding:binding-table-rules>
                <sharding:binding-table-rule logic-tables="t_order,t_order_item"/>
            </sharding:binding-table-rules>
            <sharding:broadcast-table-rules>
                <sharding:broadcast-table-rule table="t_address"/>
            </sharding:broadcast-table-rules>
        </sharding:sharding-rule>
    </sharding:data-source>
</beans>

```
custom-sharding-datebase-tables.xml 分库分表配置
注意：内部使用的算法，请参考项目代码，这里不贴出来了，下方有github项目地址
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bean="http://www.springframework.org/schema/util"
       xmlns:sharding="http://shardingsphere.apache.org/schema/shardingsphere/sharding"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd http://shardingsphere.apache.org/schema/shardingsphere/sharding http://shardingsphere.apache.org/schema/shardingsphere/sharding/sharding.xsd">

    <!--druid公共配置-->
    <import resource="classpath:META-INF/spring/druid-common.xml"/>

    <bean id="custom_ds_tb_0" parent="dataSource" init-method="init" destroy-method="close">
        <!--配置mysql -->
        <property name="url" value="${mysql.jdbc.url13}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>
    </bean>
    <bean id="custom_ds_tb_1" parent="dataSource" init-method="init" destroy-method="close">
        <property name="url" value="${mysql.jdbc.url14}"/>
        <property name="username" value="${mysql.jdbc.username}"/>
        <property name="password" value="${mysql.jdbc.password}"/>
        <property name="validationQuery" value="${mysql.jdbc.validationQuery}"/>
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}"/>
    </bean>

    <!--############标准分片配置：自定义策略#############-->

    <!--分库分表策略，inline-stragegy(不推荐),推荐使用standard-strategy便于扩展-->
    <!--<sharding:inline-strategy id="databaseStrategy" sharding-column="user_id" algorithm-expression="demo_ds_${user_id % 2}" />
    <sharding:inline-strategy id="orderTableStrategy" sharding-column="order_id" algorithm-expression="t_order_${order_id % 2}" />
    <sharding:inline-strategy id="orderItemTableStrategy" sharding-column="order_id" algorithm-expression="t_order_item_${order_id % 2}" />-->

    <!-- 分库算法：精确分片算法和范围分片算法-->
    <bean id="preciseModuloShardingDatabaseAlgorithm" class="com.zja.algorithm.PreciseModuloShardingDatabaseAlgorithm"/>
    <bean id="rangeModuloShardingDatabaseAlgorithm" class="com.zja.algorithm.RangeModuloShardingDatabaseAlgorithm"/>
    <!-- 分表算法：精确分片算法和范围分片算法-->
    <bean id="preciseModuloShardingTableAlgorithm" class="com.zja.algorithm.PreciseModuloShardingTableAlgorithm"/>
    <bean id="rangeModuloShardingTableAlgorithm" class="com.zja.algorithm.RangeModuloShardingTableAlgorithm"/>

    <!--分库策略：precise-algorithm-ref(必选)，range-algorithm-ref(可选)-->
    <sharding:standard-strategy id="databaseStrategy" sharding-column="user_id"
                                precise-algorithm-ref="preciseModuloShardingDatabaseAlgorithm"
                                range-algorithm-ref="rangeModuloShardingDatabaseAlgorithm"/>

    <!--分表策略：precise-algorithm-ref(必选)，range-algorithm-ref(可选)-->
    <sharding:standard-strategy id="orderTableStrategy" sharding-column="order_id"
                                precise-algorithm-ref="preciseModuloShardingTableAlgorithm"
                                range-algorithm-ref=""/>
    <sharding:standard-strategy id="orderItemTableStrategy" sharding-column="order_item_id"
                                precise-algorithm-ref="preciseModuloShardingTableAlgorithm"
                                range-algorithm-ref=""/>

    <bean:properties id="properties">
        <prop key="worker.id">123</prop>
    </bean:properties>

    <!--主键生成器：默认使用雪花算法生成递增趋势id-->
    <sharding:key-generator id="orderKeyGenerator" type="SNOWFLAKE" column="order_id" props-ref="properties"/>
    <sharding:key-generator id="itemKeyGenerator" type="SNOWFLAKE" column="order_item_id" props-ref="properties"/>

    <!--分片数据源-->
    <sharding:data-source id="shardingDataSource">
        <!--分片规则(分库分表)，默认数据源custom_ds_tb_0，不需要分片的数据表放到默认数据源中-->
        <sharding:sharding-rule data-source-names="custom_ds_tb_0,custom_ds_tb_1" default-data-source-name="custom_ds_tb_0">
            <sharding:table-rules>
                <!--分库分表-->
                <sharding:table-rule logic-table="t_order" actual-data-nodes="custom_ds_tb_${0..1}.t_order_${0..1}" database-strategy-ref="databaseStrategy" table-strategy-ref="orderTableStrategy" key-generator-ref="orderKeyGenerator" />
                <sharding:table-rule logic-table="t_order_item" actual-data-nodes="custom_ds_tb_${0..1}.t_order_item_${0..1}" database-strategy-ref="databaseStrategy" table-strategy-ref="orderItemTableStrategy" key-generator-ref="itemKeyGenerator" />
            </sharding:table-rules>
            <!--logic-tables逻辑表名，如果真实表为空，则把逻辑表名作为真实表名-->
            <sharding:binding-table-rules>
                <!-- 配置绑定表(分片规则相同，一般为主表子表的关系)，若不是绑定表不用配置，否则全路由不会走笛卡尔积 -->
                <sharding:binding-table-rule logic-tables="t_order,t_order_item"/>
            </sharding:binding-table-rules>
            <sharding:broadcast-table-rules>
                <sharding:broadcast-table-rule table="t_address"/>
            </sharding:broadcast-table-rules>
        </sharding:sharding-rule>
    </sharding:data-source>
</beans>

```

spring-data-jpa.xml spring集成jpa并配置"分片数据源"
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/data/jpa
       https://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

    <!--############### Sharding-jdbc配置 ###############-->

    <!--主从数据库：需要配置mysql数据库主从复制，获取从节点数据不存在抛异常-->
    <!--<import resource="classpath:META-INF/spring/sharding/application-master-slave.xml"/>-->
    <!--分库：只分库，不分表，效果友好-->
    <!--<import resource="classpath:META-INF/spring/sharding/application-sharding-databases.xml"/>-->
    <!--分库分表：效果友好-->
    <!--<import resource="classpath:META-INF/spring/sharding/application-sharding-databases-tables.xml"/>-->
    <!--主从分库分表:未测试-->
    <!--<import resource="classpath:META-INF/spring/sharding/application-sharding-master-slave.xml"/>-->
    <!--同库分表：只分表，不分库，效果友好-->
    <!--<import resource="classpath:META-INF/spring/sharding/application-sharding-tables.xml"/>-->

    <!--**************自定义分库分表算法**************-->
    <!--分库：自定义分库策略-->
    <!--分库：只分库，不分表-->
    <!--<import resource="classpath:META-INF/spring/custom/custom-sharding-database.xml"/>-->
    <!--分库分表：分库同时分表-->
    <import resource="classpath:META-INF/spring/custom/custom-sharding-datebase-tables.xml"/>
    <!--同库分表：只分表，不分库-->
    <!--<import resource="classpath:META-INF/spring/custom/custom-sharding-tables.xml"/>-->

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:properties/hibernate-jpa.properties" ignore-unresolvable="true"/>

    <!--Start jpa Config #########-->

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
        <property name="dataSource" ref="shardingDataSource"/>
        <!--<property name="dataSource" ref="masterSlaveDataSource"/>-->

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
                <!--<prop key="hibernate.hbm2ddl.auto">create-drop</prop>-->
                <!--<prop key="hibernate.hbm2ddl.auto">none</prop>-->
                <!-- 建表的命名规则： My_NAME->MyName-->
                <prop key="hibernate.ejb.naming_strategy">${hibernate.ejb.naming_strategy}</prop>
            </props>
        </property>
    </bean>

    <!--扫描dao包-->
    <!--<jpa:repositories base-package="com.zja.dao" entity-manager-factory-ref="entityManagerFactory" transaction-manager-ref="transactionManager" />-->
    <jpa:repositories base-package="com.zja.dao"/>

    <!-- Jpa 事务配置 -->
    <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>

    <!--XML配置事务声明方式 开启注解声明事务 -->
    <tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true"/>

    <!--End jpa Config ########-->

</beans>

```
>1.修改数据源`dataSource`配置,使用不同的分片数据源
>2.需要修改实体类扫描包和dao接口扫描包路径

### 3、单元测试(sharding jdbc 分库分表测试)

分库分表测试：分库分表/只分库/只分表 等三种情况
注：实体类代码请参考项目
```java
import com.zja.dao.OrderJpaRepositories;
import com.zja.entity.Order;
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
import java.util.List;
import java.util.Optional;

/**
 * Date: 2019-12-17 15:00
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoOrderTest {

    @Autowired
    private OrderJpaRepositories jpaRepositories;

    @Test
    public void saveAll() {

        List<Order> orders = new ArrayList<>();

        for (int i=1;i<=10;i++){
            Order order = new Order();
            order.setOrderId(i+0L);
            order.setUserId(i);
            order.setStatus("true");
            order.setAddressId(i+0L);

            orders.add(order);
        }

        List<Order> jpas = this.jpaRepositories.saveAll(orders);
        System.out.println(jpas);
    }

    @Test
    public void save(){
        Order order = new Order();
        //order_id为偶数，插入t_order_1
        //order_id为奇数，插入t_order_2
        order.setOrderId(1);

        order.setUserId(32);
        order.setStatus("true");
        order.setAddressId(32L);
        Order save = this.jpaRepositories.save(order);
        System.out.println(save);
    }

    @Test
    public void Sort(){
        Sort sort =new Sort(Sort.Direction.ASC,"userId");
        List<Order> orderList = jpaRepositories.findAll(sort);
        System.out.println(orderList);
    }

    @Test
    public void Pageable(){
        Sort sort =new Sort(Sort.Direction.ASC,"userId");
        Pageable pageable = PageRequest.of(0,5,sort);
        Page<Order> orderPage = jpaRepositories.findAll(pageable);
        List<Order> orderList = orderPage.getContent();
        System.out.println(orderList);
    }
}

```

广播表测试：每个库中都有此表，并且表数据都是完整的，适合表数据特别少的情况
注：实体类代码请参考项目
```java
import com.zja.dao.AddressJpaRepositories;
import com.zja.entity.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019-12-18 16:35
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoAddressTest {

    @Autowired
    private AddressJpaRepositories jpaRepositories;

    @Test
    public void saveAll(){
        List<Address> addresses = new ArrayList<>();
        for (int i=1;i<=10;i++){
            Address address = new Address();
            address.setAddressId(i+0L);
            address.setAddressName("Name"+i);
            addresses.add(address);
        }
        //由于t_address是广播表，当插入的数据，会存到每个库中的t_address表中，每张表都有完整的表数据
        List<Address> addressList = jpaRepositories.saveAll(addresses);
        System.out.println(addressList);
    }

    @Test
    public void findAll(){
        List<Address> addressList = this.jpaRepositories.findAll();
        System.out.println(addressList);
    }
}

```


### 4、项目的github和简书博客地址
**github:**
- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)

**博客:**
- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金：https://juejin.im/user/5d82daeef265da03ad14881b/posts
