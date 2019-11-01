# spring5.x-task

[TOC]

spring5.x-task此模块是从spring5x-base 基础模块扩展过来的

spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](spring5.x-base.md)



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、Spring TaskScheduler
- 2、Spring Quartz



> 文档：[scheduling](https://links.jianshu.com/go?to=https%3A%2F%2Fdocs.spring.io%2Fspring-framework%2Fdocs%2Fcurrent%2Fspring-framework-reference%2Fintegration.html%23scheduling)
 Quartz官网：[http://www.quartz-scheduler.org/](https://links.jianshu.com/go?to=http%3A%2F%2Fwww.quartz-scheduler.org%2F)
 cron表达式生成器：[http://cron.qqe2.com/](https://links.jianshu.com/go?to=http%3A%2F%2Fcron.qqe2.com%2F)

**Spring Task 对比 Spring Quartz**
 1、Spring Task默认单线程串行执行任务，多任务时若某个任务执行时间过长，后续任务会无法及时执行
 2、Quartz采用多线程，下一个调度时间到达时，会另起一个线程执行调度，不会发生阻塞问题，但调度过多时可能导致数据处理异常
 3、Spring Task抛出异常后，同一个任务后续不再触发
 4、Spring Quartz抛出异常后，同一个任务后续仍然会触发



### 1、Spring TaskScheduler

****

pom.xml 

> 只需要spring相关依赖,下面会有的pom.xml 完整配置 依赖



Task.java

```java
package com.zja.task;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

/**
 * @author ZhengJa
 * @description Task 定时任务类
 * @data 2019/10/31
 */
public class Task {

    public void methodA() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodA 方法执行", LocalDateTime.now()));
    }

    @Async
    public void methodB() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodB 方法执行", LocalDateTime.now()));
        Thread.sleep(10 * 1000);
    }

    public void methodC() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodC 方法执行", LocalDateTime.now()));
    }

}

```

> 任务类



spring-task.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task.xsd">

    <!--spring-task 定时任务xml 配置-->

    <!--配置定时任务-->
    <bean id="task" class="com.zja.task.Task"/>
    <task:scheduled-tasks scheduler="myScheduler">
        <!--基于间隔的触发器，其中间隔是从上一个任务的  完成时间  开始计算, 时间单位值以毫秒为单位。-->
        <task:scheduled ref="task" method="methodA" fixed-delay="5000" initial-delay="1000"/>
        <!--基于间隔的触发器，其中间隔是从上一个任务的  开始时间  开始测量的。-->
        <task:scheduled ref="task" method="methodB" fixed-rate="5000"/>
        <!-- cron 表达式-->
        <task:scheduled ref="task" method="methodC" cron="0/10 * * * * ?"/>
    </task:scheduled-tasks>

    <!--定义任务调度器线程池的大小-->
    <task:scheduler id="myScheduler" pool-size="10"/>

    <!--定义任务执行器的线程池大小、等待队列的容量、和拒绝策略-->
    <task:executor
            id="executorWithPoolSizeRange"
            pool-size="5-25"
            queue-capacity="100"
            rejection-policy="CALLER_RUNS"/>
    <!--拒绝策略默认值为 ABORT
        CALLER_RUNS 来限制入栈任务
        DISCARD 删除当前任务
        DISCARD_OLDEST 将任务放在队列的头部。-->

    <!--允许在任何 Spring 管理的对象上检测@Async 和@Scheduled 注释, 如果存在，将生成用于异步执行带注释的方法的代理。-->
    <task:annotation-driven/>

</beans>

```

>关于调度程序线程池作用说明：
>
>如上 methodA 、 methodB 、methodC 三个方法，其中 methodB 是耗时方法，如果不声明调度程序线程池则 methodB 会阻塞 methodA 、methodC 方法的执行 因为调度程序是单线程的。
>
>关于任务执行线程池作用说明：
>
>按照上面的例子，如果我们声明 methodB 是按照 fixedRate=5000 方法执行的 ，理论上不管任务耗时多久，任务都应该是每 5 秒执行一次，但是实际上任务是被加入执行队列，并不会立即被执行，因为默认执行任务是单线程的。这个时候需要开启@EnableAsync 注解并指定方法是 @Async 异步的，并且配置执行任务线程池 (如果不配置就使用默认的线程池配置)。



spring-mvc.xml 

```xml
	<!--spring-task 定时任务 xml配置-->
    <import resource="classpath:META-INF/spring/spring-task.xml"/>

```

> 下方会有spring-mvc.xml 完整配置



启动项目，控制台打印效果：

```python
线程名称：SimpleAsyncTaskExecutor-1 ; 线程 ID：66 ; 调用方法：methodB 方法执行 ; 调用时间：2019-10-31T09:25:22.339
线程名称：myScheduler-3 ; 线程 ID：65 ; 调用方法：methodA 方法执行 ; 调用时间：2019-10-31T09:25:23.106
线程名称：SimpleAsyncTaskExecutor-2 ; 线程 ID：70 ; 调用方法：methodB 方法执行 ; 调用时间：2019-10-31T09:25:27.092
线程名称：myScheduler-1 ; 线程 ID：63 ; 调用方法：methodA 方法执行 ; 调用时间：2019-10-31T09:25:28.108
线程名称：myScheduler-4 ; 线程 ID：67 ; 调用方法：methodC 方法执行 ; 调用时间：2019-10-31T09:25:30.002
线程名称：SimpleAsyncTaskExecutor-3 ; 线程 ID：74 ; 调用方法：methodB 方法执行 ; 调用时间：2019-10-31T09:25:32.093
线程名称：myScheduler-5 ; 线程 ID：69 ; 调用方法：methodA 方法执行 ; 调用时间：2019-10-31T09:25:33.110
线程名称：SimpleAsyncTaskExecutor-4 ; 线程 ID：77 ; 调用方法：methodB 方法执行 ; 调用时间：2019-10-31T09:25:37.093
线程名称：myScheduler-6 ; 线程 ID：71 ; 调用方法：methodA 方法执行 ; 调用时间：2019-10-31T09:25:38.112
线程名称：myScheduler-1 ; 线程 ID：63 ; 调用方法：methodC 方法执行 ; 调用时间：2019-10-31T09:25:40.002
线程名称：SimpleAsyncTaskExecutor-5 ; 线程 ID：78 ; 调用方法：methodB 方法执行 ; 调用时间：2019-10-31T09:25:42.093

```

**Spring Task 已完结**



### 2、Spring Quartz

****

pom.xml

```xml
	<properties>
	<!--quart定时任务 版本-->
        <quartz.version>2.3.1</quartz.version>
    </properties>
	
	   <!--定时任务quartz-->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>${quartz.version}</version>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
            <version>${quartz.version}</version>
        </dependency>

```

QuartzTask.java 测试类

```java
package com.zja.quartz;

import java.util.Date;

/**
 * @author ZhengJa
 * @description quartz 定时任务类 Job类
 * @data 2019/10/31
 */
public class QuartzTask {

    //执行方法
    public void execute(){
        System.out.println("控制台打印 时间: "+new Date());
    }
}


```

spring-quartz.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd"
       default-autowire="byType">

    <!--1、 配置Job类：也可通过注解bean配置 -->
    <bean id="quartzTask" class="com.zja.quartz.QuartzTask"/>

    <!--2、控制台打印 任务JobDetail-->
    <bean id="consolePrintJob" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
        <property name="targetObject" ref="quartzTask" />          <!--执行类的实例-->
        <property name="targetMethod" value="execute" />                   <!--执行方法-->
        <property name="concurrent" value="false" />    <!-- 是否并发执行 -->
        <property name="name" value="print" />      <!-- 任务名称-->
        <property name="group" value="console" /> <!-- 任务组-->
        <property name="arguments">   <!--参数列表-->
            <list></list>
        </property>
    </bean>
    
    <!-- 3、简单触发器SimpleTrigger：只支持按照一定频度调用任务，如每20秒执行一次等 -->
    <bean id="consolePrintSimpleTrigger" class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean">
        <property name="jobDetail" ref="consolePrintJob"/>
        <property name="startDelay" value="1000"/>  <!-- 1秒后开始 -->
        <property name="repeatInterval" value="20000"/>  <!-- 每20秒执行一次 -->
    </bean>

    <!--3、控制台打印 触发器CronTrigger：支持到指定时间运行一次，如每天12:00运行一次等，比较强大-->
    <bean id="consolePrintCronTrigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
        <property name="jobDetail" ref="consolePrintJob" />
        <!--cron表达式在线生成地址：http://cron.qqe2.com/ -->
        <property name="cronExpression" value="0 0/1 * * * ? " />       <!--测试，从0开启每分钟执行一次-->
        <!--<property name="cronExpression" value="0 0 3 * * ?" />-->        <!--每天凌晨3点执行一次-->
    </bean>

    <!-- 4、调度器scheduler -->
    <bean id="schedulerFactoryBean" class="org.springframework.scheduling.quartz.SchedulerFactoryBean"
          autowire="no">
        <property name="triggers">
            <list>
                <!--SimpleTrigger 触发器测试-->
                <ref bean="consolePrintSimpleTrigger" />
                <!--CronTrigger 触发器测试-->
                <ref bean="consolePrintCronTrigger" />
            </list>
        </property>
    </bean>
</beans>


```

> **quartz 触发器有两种：**
> 1、SimpleTrigger 触发器测试：只支持按照一定频度调用任务，如每20秒执行一次等，功能单一
> 2、CronTrigger 触发器测试：支持到指定时间运行一次，如每天12:00运行一次等，功能强大，推荐

spring-mvc.xml

```xml
    <!--spring quartz定时任务 xml配置-->
    <import resource="classpath:META-INF/spring/spring-quartz.xml"/>

```



启动项目，查看控制台打印：

- CronTrigger 触发器测试

```python
控制台打印 时间: Thu Oct 31 10:24:00 CST 2019
控制台打印 时间: Thu Oct 31 10:25:00 CST 2019
控制台打印 时间: Thu Oct 31 10:26:00 CST 2019

```

>  cron表达式：0 0/1 * * * ?   # 从0开始，每分钟执行一次！！！

- SimpleTrigger 触发器测试

```python
控制台打印 时间: Thu Oct 31 10:44:10 CST 2019
控制台打印 时间: Thu Oct 31 10:44:30 CST 2019
控制台打印 时间: Thu Oct 31 10:44:50 CST 2019

```



**到此搭建已经完成！！！** 下面是完整配置



pom.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zja</groupId>
    <artifactId>spring5x-task</artifactId>
    <packaging>war</packaging>

    <name>spring5x-task</name>

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
        <!--quart定时任务 版本-->
        <quartz.version>2.3.1</quartz.version>
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

        <!--jackson  默认采用 Jackson 将对象进行序列化和反序列化-->
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

        <!--测试相关依赖-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!--定时任务quartz-->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>${quartz.version}</version>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
            <version>${quartz.version}</version>
        </dependency>

    </dependencies>

    <build>
        <finalName>spring5x-task</finalName>
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
            location="classpath:application.properties" />

    <!--spring task定时任务 xml配置-->
    <!--<import resource="classpath:META-INF/spring/spring-task.xml"/>-->
    <!--spring quartz定时任务 xml配置-->
    <import resource="classpath:META-INF/spring/spring-quartz.xml"/>

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





## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)



## 博客地址

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts