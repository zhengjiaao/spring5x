![spring5.x-redis-目录.png](https://upload-images.jianshu.io/upload_images/15645795-9ad2dfed94a2fedd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


spring5.x-springmvc此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](spring5.x-base.md)

windows redis集群搭建参考：https://www.jianshu.com/p/a3721ab14a9a



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、jeids 连接redis单机配置
- 2、jedis 连接redis集群配置
- 3、redisson 连接redis单机配置
- 4、redisson 连接redis集群配置

Redis客户端
关于 spring 整合 Redis 本用例提供两种整合方法：
Jedis: 官方推荐的 java 客户端，能够胜任 Redis 的大多数基本使用；

Redisson：也是官方推荐的客户端，比起 Redisson 提供了更多高级的功能，如分布式锁、集合数据切片等功能。同时提供了丰富而全面的中英文版本的说明文档。




### 1、jeids 连接redis单机配置

*****

pom.xml 引入jedis依赖

```xml
		<!--jedis-->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>3.0.0</version>
        </dependency>

```

jedis.properties

```properties
redis.host=127.0.0.1
redis.port=6379
# 连接超时时间
redis.timeout=2000
# 最大空闲连接数
redis.maxIdle=8
# 最大连接数
redis.maxTotal=16

```

jedis.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--单机版配置-->
    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:jedis.properties" ignore-unresolvable="true"/>

    <!--初始化连接池配置-->
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.maxIdle}"/>
        <property name="maxTotal" value="${redis.maxTotal}"/>
    </bean>

    <!--配置 jedis 连接池-->
    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg name="poolConfig" ref="jedisPoolConfig"/>
        <constructor-arg name="host" value="${redis.host}"/>
        <constructor-arg name="port" value="${redis.port}"/>
        <constructor-arg name="timeout" value="${redis.timeout}"/>
    </bean>

    <!--把 jedis 创建与销毁交给 spring 来管理-->
    <bean id="jedis" factory-bean="jedisPool" factory-method="getResource" destroy-method="close"/>

</beans>

```

spring-mvc.xml

```xml
	<!--jedis 连接 redis 单机配置-->
    <import resource="classpath:META-INF/spring/jedis.xml"/>

```

JedisController.java   **jedis 连接redis 单机配置测试**

```java
package com.zja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

/**
 * @author ZhengJa
 * @description Jedis 连接 redis 单机配置测试
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/jedis")
public class JedisController {

    @Autowired
    private Jedis jedis;


    //存数据
    @GetMapping("set/data")
    public Object Set() {
        String value = jedis.set("hello", "我是value");
        return value;
    }

    //获取数据
    @GetMapping("get/data")
    public Object Get() {
        String value = jedis.get("hello");
        return value;
    }

    //设置过期时间
    @GetMapping("get/overdue")
    public Object setEx() {
        String value = jedis.setex("hello", 10, "我会在 10 秒后过期");
        return value;
    }

}

```

访问：

存数据到redis：http://localhost:8080/spring5x-redis/rest/jedis/set/data

取数据：http://localhost:8080/spring5x-redis/rest/jedis/get/data

设置存数据并设置过期时间：http://localhost:8080/spring5x-redis/rest/jedis/get/overdue

取数据试试：http://localhost:8080/spring5x-redis/rest/jedis/get/data



### 2、jedis 连接redis集群配置

****

pom.xml 引入jedis依赖

```xml
		<!--jedis-->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>3.0.0</version>
        </dependency>

```

jedisCluster.properties

```properties
#集群公共配置
# 连接超时时间
redis.timeout=2000
# 最大空闲连接数
redis.maxIdle=8
# 最大连接数
redis.maxTotal=16

#集群节点配置
redis.host_1=127.0.0.1
redis.port_1=6382

redis.host_2=127.0.0.1
redis.port_2=6383

redis.host_3=127.0.0.1
redis.port_3=6384

```

jedisCluster.xml 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:jedisCluster.properties" ignore-unresolvable="true"/>

    <!--初始化连接池配置-->
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.maxIdle}"/>
        <property name="maxTotal" value="${redis.maxTotal}"/>
    </bean>

    <!--配置 jedis 连接池 (集群)-->
    <bean id="jedisCluster" class="redis.clients.jedis.JedisCluster">
        <constructor-arg name="nodes">
            <set>
                <bean class="redis.clients.jedis.HostAndPort">
                    <constructor-arg name="host" value="${redis.host_1}"/>
                    <constructor-arg name="port" value="${redis.port_1}"/>
                </bean>
                <bean class="redis.clients.jedis.HostAndPort">
                    <constructor-arg name="host" value="${redis.host_2}"/>
                    <constructor-arg name="port" value="${redis.port_2}"/>
                </bean>
                <bean class="redis.clients.jedis.HostAndPort">
                    <constructor-arg name="host" value="${redis.host_3}"/>
                    <constructor-arg name="port" value="${redis.port_3}"/>
                </bean>
            </set>
        </constructor-arg>
        <constructor-arg name="timeout" value="${redis.timeout}"/>
    </bean>

</beans>

```

spring-mvc.xml 

```xml
    <!--jedis 连接 redis 集群配置-->
    <import resource="classpath:META-INF/spring/jedisCluster.xml"/>

```

JedisClusterController.java   **jedis 连接 redis集群配置测试**

```java
package com.zja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author ZhengJa
 * @description Jedis 连接 redis 单机配置测试
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/jediscluster")
public class JedisClusterController {

    @Autowired
    private JedisCluster jedisCluster;


    //存数据
    @GetMapping("set/data")
    public Object Set() {
        String value = jedisCluster.set("hello", "我是value");
        return value;
    }

    //获取数据
    @GetMapping("get/data")
    public Object Get() {
        String value = jedisCluster.get("hello");
        return value;
    }

    //设置过期时间
    @GetMapping("get/overdue")
    public Object setEx() {
        String value = jedisCluster.setex("hello", 10, "我会在 10 秒后过期");
        return value;
    }

}


```

访问：

存数据到redis：http://localhost:8080/spring5x-redis/rest/jediscluster/set/data

取数据：http://localhost:8080/spring5x-redis/rest/jediscluster/get/data

设置存数据并设置过期时间：http://localhost:8080/spring5x-redis/rest/jediscluster/get/overdue

取数据试试：http://localhost:8080/spring5x-redis/rest/jediscluster/get/data





### 3、redisson 连接redis单机配置

****

pom.xml

```xml
		<!--redisson-->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.9.1</version>
        </dependency>
        <!--redisson 中的部分功能依赖了netty  -->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.32.Final</version>
        </dependency>

```

redisson.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://redisson.org/schema/redisson http://redisson.org/schema/redisson/redisson.xsd">

    <redisson:client>
        <!--更多可配置项见官方文档 2.6.2. 通过 JSON、YAML 和 Spring XML 文件配置单节点模式
         <a href="https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#26-%E5%8D%95redis%E8%8A%82%E7%82%B9%E6%A8%A1%E5%BC%8F"> -->
        <redisson:single-server
                address="redis://127.0.0.1:6379"
                idle-connection-timeout="10000"
                ping-timeout="1000"
                connect-timeout="10000"
                timeout="3000"
                retry-attempts="3"
                retry-interval="1500"
                connection-minimum-idle-size="10"
                connection-pool-size="64"
                database="2"
        />
    </redisson:client>

</beans>

```

spring-mvc.xml

```xml
    <!--redisson 连接redis 单机配置-->
    <import resource="classpath:META-INF/spring/redisson.xml"/>

```

RedissonController.java  **redisson 连接redis 单机配置测试**

```java
package com.zja.controller;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author ZhengJa
 * @description Redisson 连接redis 单机配置测试
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/redisson")
public class RedissonController {

    @Autowired
    private RedissonClient redissonClient;

    //存数据
    @GetMapping("set/data")
    public Object Set() {
        // key 存在则更新 不存在则存入
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        rBucket.set("firstValue");

        //redissonClient.shutdown();
        return rBucket.get();
    }

    //获取数据
    @GetMapping("get/data")
    public Object Get() {
        RBucket<String> rBucket = redissonClient.getBucket("redisson");

        //redissonClient.shutdown();
        return rBucket.get();

    }

    //设置过期时间
    @GetMapping("get/overdue")
    public Object SetEx() {
        // key 存在则更新 不存在则存入，并设置过期时间
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        rBucket.set("我在十秒后会消失", 10, TimeUnit.SECONDS);

        //redissonClient.shutdown();
        return "我在十秒后会消失";

    }

}


```

> 存基本数据类型





### 4、redisson 连接redis集群配置

****

pom.xml

```xml
		<!--redisson-->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.9.1</version>
        </dependency>
        <!--redisson 中的部分功能依赖了netty  -->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.32.Final</version>
        </dependency>

```

redissonCluster.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://redisson.org/schema/redisson http://redisson.org/schema/redisson/redisson.xsd">

    <!-- 最基本配置 -->
    <redisson:client>
        <!--集群更多配置参数见官方文档 2.4.2 通过 JSON、YAML 和 Spring XML 文件配置集群模式
         <a href="https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#24-%E9%9B%86%E7%BE%A4%E6%A8%A1%E5%BC%8F"> -->
        <redisson:cluster-servers>
            <redisson:node-address value="redis://127.0.0.1:6382"/>
            <redisson:node-address value="redis://127.0.0.1:6383"/>
            <redisson:node-address value="redis://127.0.0.1:6384"/>
        </redisson:cluster-servers>
    </redisson:client>

</beans>

```

spring-mvc.xml

```xml
	<!--redisson 连接redis 集群配置-->
    <import resource="classpath:META-INF/spring/redissonCluster.xml"/>

```

RedissonClusterController.java  **redisson 连接redis 集群配置测试**

```java
package com.zja.controller;

import com.zja.entity.UserEntity;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhengJa
 * @description Redisson 连接redis 单机配置测试
 * @data 2019/10/29
 */
@RestController
@RequestMapping("rest/redissoncluster")
public class RedissonClusterController {

    @Autowired
    private RedissonClient redissonClient;

    //存数据
    @GetMapping("set/data")
    public Object Set() {
        // key 存在则更新 不存在则存入
        RBucket<UserEntity> rBucket = redissonClient.getBucket("userentity");
        rBucket.set(new UserEntity("张三","22",new Date()));

        //redissonClient.shutdown();
        return rBucket.get();
    }

    //获取数据
    @GetMapping("get/data")
    public Object Get() {
        RBucket<UserEntity> rBucket = redissonClient.getBucket("userentity");

        //redissonClient.shutdown();
        return rBucket.get();

    }

    //设置过期时间
    @GetMapping("get/overdue")
    public Object SetEx() {
        // key 存在则更新 不存在则存入，并设置过期时间
        RBucket<UserEntity> rBucket = redissonClient.getBucket("userentity");
        rBucket.set(new UserEntity("十秒后过期","22",new Date()), 10, TimeUnit.SECONDS);

        //redissonClient.shutdown();
        return "我在十秒后会消失";

    }

}


```

> 存对象  返回：{ "name" : "张三", "age" : "22", "date" : "2019-10-29" }

其中 UserEntity.java 实体类

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



**到此所有的配置已完成！** 下面时配置文件的完整版！



## spring-mvc.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

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
            location="classpath:application.properties" ignore-unresolvable="true"/>

    <!--jedis 连接 redis 单机配置-->
    <import resource="classpath:META-INF/spring/jedis.xml"/>
    <!--jedis 连接 redis 集群配置-->
    <import resource="classpath:META-INF/spring/jedisCluster.xml"/>

    <!--redisson 连接redis 单机配置-->
    <!--<import resource="classpath:META-INF/spring/redisson.xml"/>-->
    <!--redisson 连接redis 集群配置-->
    <import resource="classpath:META-INF/spring/redissonCluster.xml"/>



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

## pom.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zja</groupId>
    <artifactId>spring5x-redis</artifactId>
    <packaging>war</packaging>

    <name>spring5x-redis</name>

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

        <!--jackson 解决 spring-mvc.xml 配置的MappingJackson2HttpMessageConverter 错误-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.4</version>
        </dependency>

        <!--jedis-->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>3.0.0</version>
        </dependency>

        <!--redisson-->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.9.1</version>
        </dependency>
        <!--redisson 中的部分功能依赖了netty  -->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.32.Final</version>
        </dependency>

    </dependencies>

    <build>
        <finalName>spring5x-redis</finalName>
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

## web.xml 完整配置

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

**到此搭建已经完成！！！**





## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)




## 博客地址

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts