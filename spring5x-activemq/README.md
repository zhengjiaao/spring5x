![spring5.x-activemq.png](https://upload-images.jianshu.io/upload_images/15645795-982a7d0230d84406.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 一、介绍一下ActiveMQ：

****

消费消息有两种模式：推送和拉取。推送模式的适用场景是，消息的消费者能力强于生产者，一旦有消息就快速进行消费，不会堆积，也没有延迟，。拉取是消息消费者主动向消息中介（broker）发起请求，获取消息。拉取模式的优点是消费者可以自己控制消息的拉取速度，消息中介不需要维护消费者的状态。如果总是从消息中介推送消息，消费者能力不如生产者时，消费者会被压垮或者必须在消息中介使用定时消息推送，增加消息中介的复杂度。缺点是消息及时性差，取决于拉取的间隔。而且有可能是空拉取，造成资源浪费。

​       拉取模式的使用场景是，消息的生产者能力强于消费者，服务器在高峰时间允许堆积消息，然后在波谷时间完成消费。

​       因为ActiveMQ采用消息推送方式，所以最适合的场景是默认消息都可在短时间内被消费。数据量越大，查找和消费消息就越慢，消息积压程度与消息速度成反比。

​       ActiveMQ的缺点：

​       1.吞吐量低。由于ActiveMQ需要建立索引，导致吞吐量下降。这是无法克服的缺点，只要使用完全符合JMS规范的消息中间件，就要接受这个级别的TPS。

​       2.无分片功能。这是一个功能缺失，JMS并没有规定消息中间件的集群、分片机制。而由于ActiveMQ是伟企业级开发设计的消息中间件，初衷并不是为了处理海量消息和高并发请求。如果一台服务器不能承受更多消息，则需要横向拆分。ActiveMQ官方不提供分片机制，需要自己实现。

​       ActiveMQ的适用场景：

​       1.业务系统没有实现幂等性。消费不成功，消息连同业务数据一起回滚，适用于不易实现幂等性的复杂业务场景或敏感性业务。

​       2.强事务一致性。消息和业务数据必须处于同一事务状态，假如业务数据回滚，消息必须也回滚成未消费状态。

​       3.内部系统。对于TPS要求低的系统，ActiveMQ由于使用简单，完全支持JMS，非常适合快速开发。并且ActiveMQ有完善的监控机制和操作界面。

​       ActiveMQ不适用的场景：

​       1.性能要求高，且不要求事务。性能是ActiveMQ的短板，如果业务要求消息中间件的性能很高，且不要求强一致性的事务，则不应使用ActiveMQ。

​        2.消息量巨大的场景。ActiveMQ不支持消息自动分片机制，如果消息量巨大，导致一台服务器不能处理全部消息，就需要自己开发消息分片功能。

下表是文章中列举的常用消息中间件的对比：

|                 | **ActiveMQ**                               | **RabbitMQ**                               | **Kafka**                                  | **RocketMQ**                                      | **HornetQ**           |
| --------------- | ------------------------------------------ | ------------------------------------------ | ------------------------------------------ | ------------------------------------------------- | --------------------- |
| **版本号**      | 5.10.0                                     | 3.3.4                                      | 0.8.1                                      | 3.1.9-SNAPSHOT                                    | 2.4.0                 |
| **关注度**      | 高                                         | 高                                         | 高                                         | 高                                                | 中                    |
| **成熟度**      | 成熟                                       | 成熟                                       | 比较成熟                                   | 不成熟                                            | 成熟                  |
| **社区活跃度**  | 高                                         | 高                                         | 中                                         | 中                                                | 中                    |
| **文档**        | 多                                         | 多                                         | 中                                         | 少                                                | 中                    |
| **开发语言**    | Java                                       | Erlang                                     | Scala                                      | Java                                              | Java                  |
| **JMS****支持** | 是                                         | 需付费                                     | 否                                         | 第三方提供                                        | 是                    |
| **协议支持**    | AMQP MQTT STOMP REST等                     | AMQP MQTT STOMP REST等                     | 自定义                                     | 自定义                                            | AMQP STOMP REST等     |
| **客户端支持**  | Java、C、 C++、Python、 PHP、Perl、 .Net等 | Java、C、 C++、Python、 PHP、Perl、 .Net等 | Java、C、 C++、Python、 PHP、Perl、 .Net等 | Java、C++                                         | Java                  |
| **持久化**      | 内存 文件 数据库                           | 内存 文件                                  | 文件                                       | 文件                                              | 内存 文件             |
| **事务**        | 支持                                       | 支持                                       | 不支持                                     | 不完全支持                                        | 支持                  |
| **集群**        | 一般                                       | 较好                                       | 好                                         | 好                                                | 较好                  |
| **管理界面**    | 有                                         | 有                                         | 第三方提供                                 | 第三方提供                                        | 有                    |
| **亮点**        | JMS标准 成功案例多                         | 吞吐量略高于ActiveMQ                       | 吞吐量极高 批量处理                        | 初步支持分布式事务 吞吐量不低于Kafka              | JMS标准 完美整合Jboss |
| **缺点**        | 吞吐量低 无消息分片功能                    | 吞吐量低 不支持JMS                         | 不支持JMS 不支持事务                       | 不成熟 分布式事务未开发完全 监控界面不完善 文档少 | 同ActiveMQ            |

 



### 二、单机安装

------

官网下载：http://activemq.apache.org/

windows版本的，解压后进入D:\Activemq\apache-activemq-5.15.8\bin\win64 下 

双击启动：activemq.bat

访问：http://localhost:8161/admin/

输入密码和用户-需要配置

找到：D:\Activemq\apache-activemq-5.15.8\conf\jetty-realm.properties 文件，该文件保存着用户名和密码信息）

```properties
# Defines users that can access the web (console, demo, etc.)
# username: password [,rolename ...]
admin: admin, admin
user: user, user

```

> 用户名:密码,角色



找到：D:\Activemq\apache-activemq-5.15.8\conf\jetty.xml 文件，角色信息的定义

```xml
    <bean id="securityConstraint" class="org.eclipse.jetty.util.security.Constraint">
        <property name="name" value="BASIC" />
        <!--角色信息的定义-->
        <property name="roles" value="user,admin" />
        <!-- set authenticate=false to disable login -->
        <property name="authenticate" value="true" />
    </bean>

```



安装成windows服务：

**进入：D:\Activemq\apache-activemq-5.15.8\bin\win64\ **

以管理员身份运行InstallService.bat：

如果正确运行InstallService.bat发现命令行窗口一闪而过的话，可以再运行一次InstallService.bat，如果出现下面的内容，就说明已经安装成功了。

```cmd
wrapper  | CreateService failed - 指定的服务已存在。 (0x431)

```

注意：如果不以管理员身份运行，会报出

```cmd
wrapper  | OpenSCManager failed - 拒绝访问。 (0x5)
```

【wrapper.exe"' 不是内部或外部命令,也不是可运行的程序】错误。



**activeMQ的两个默认端口8161和61616的区别：**

> 8161是后台管理系统(url中访问后台管理页用此端口)，61616是给java用的tcp端口





### 三、项目搭建

****



spring5.x-activemq 此模块是从spring5x-base 基础模块扩展过来的

spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)



**项目搭建**

**基于spring5x-base 基础模块 新增功能：**

- 1、ActiveMQ 的 Queue 队列模式
- 2、ActiveMQ 的 Topic 主题模式



#### 前提  activemq依赖

****

pom.xml 引入activemq依赖

```XML
	   <!--ActiveMQ 消息中间插件-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <!--jms连接工厂-->
        <dependency>
            <groupId>javax.jms</groupId>
            <artifactId>javax.jms-api</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-core</artifactId>
            <version>5.7.0</version>
            <exclusions>
                <exclusion>
                    <artifactId>spring-context</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
            </exclusions>
        </dependency>

```



#### 1、ActiveMQ 的 Queue 队列模式

****

common.xml   是queue和topic的公共配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:jsm="http://www.springframework.org/schema/jms"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/jms
       http://www.springframework.org/schema/jms/spring-jms.xsd">

    <!-- ==========================一、生产者和消费者的公共配置 ：连接池-目的地-模式============================  -->

    <!-- 1、ActiveMQ 连接工厂 ConnectionFactory -->
    <bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
        <property name="brokerURL" value="tcp://192.168.2.42:61616"/>
        <!-- 是否异步发送 -->
        <property name="useAsyncSend" value="true" />
        <!-- Session缓存数量 -->
        <property name="sessionTaskRunner" value="10" />
    </bean>

    <!--Spring Caching 连接工厂(类似数据库线程池的东西，减少连接的创建。) -->
    <!-- 2、由于jmsTemplate每次发送消息都需要创建连接和创建session了，所以引入这个类似连接池的连接工厂，优化Mq的性能 -->
    <bean id="connectionFactoty" class="org.springframework.jms.connection.SingleConnectionFactory">
        <!-- 目标连接工厂 指向 ActiveMq工厂 -->
        <property name="targetConnectionFactory" ref="targetConnectionFactory"/>
    </bean>

    <!-- 3、ActiveMQ提供的目的地 -->

    <!--队列模式_文本消息：一个队列的目的地，点对点的-->
    <bean id="queueTextDestination" class="org.apache.activemq.command.ActiveMQQueue">
        <!-- 设置Text消息队列的名字 -->
        <constructor-arg value="queue.Text"/> <!--队列 构造函数参数-->
    </bean>

    <!--队列模式_对象消息：一个队列的目的地，点对点的-->
    <bean id="queueObjectDestination" class="org.apache.activemq.command.ActiveMQQueue">
        <!-- 设置对象消息队列的名字 -->
        <constructor-arg value="queue.Object"/> <!--队列 构造函数参数-->
    </bean>

    <!--主题模式_文本消息：一个主题目的地，发布订阅模式-->
    <bean id="topicTextDestination" class="org.apache.activemq.command.ActiveMQTopic">
        <!-- 设置Text消息主题的名字 -->
        <constructor-arg value="topic.Text"/> <!--主题 构造函数参数-->
    </bean>

    <!--主题模式_对象消息：一个主题目的地，发布订阅模式-->
    <bean id="topicObjectDestination" class="org.apache.activemq.command.ActiveMQTopic">
        <!-- 设置对象消息主题的名字 -->
        <constructor-arg value="topic.Object"/> <!--主题 构造函数参数-->
    </bean>

</beans>
```

> 公共配置是完整版，就不一点点添加了，不用的也不会报错。

**生产者配置**

ProducerService.java 生产者接口：消息发送

```java
package com.zja.activemq.producer;

/**
 * @program: jms-spring
 * @Date: 2018/11/30 17:31
 * @Author: Mr.Zheng
 * @Description:
 */
public interface ProducerService {

    //Queue 队列发送Text消息
    void sendQueueTextMessage(String message);
}


```

ProducerServiceImpl.java 生产者实现类：消息发送的实现

```java
package com.zja.activemq.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * 生产者：队列和主题
 * @Author: Mr.Zheng
 * @Description: 生产者：发送消息被消费者监听器监听到并接收消息
 */
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsQueueTemplate;

    //queueDestination 队列
    @Resource(name="queueTextDestination")
    Destination destinationQueueText;

    /**
     * Queue 发送Text类型消息
     * @param message Text消息
     * @return void
     */
    @Override
    public void sendQueueTextMessage(final String message) {
        //使用JmsTemplate发送消息
        jmsQueueTemplate.send(destinationQueueText, new MessageCreator() {
            //创键一个消息，并没有发送
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage =session.createTextMessage(message);
                return textMessage;
            }
        });
        System.out.println("Queue发送的TextMessage消息内容："+message);
    }

}


```

producer.xml  生产者：消息发送者

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--导入公共配置-->
    <import resource="common.xml"/>

    <!-- ==========================二、生产者 ：发送消息============================  -->
    <!--生产者：发送消息-->
    <bean class="com.zja.activemq.producer.ProducerServiceImpl"/>

    <!--queue(队列模式)：配置jmsQueueTemplate，用于发送mq消息-->
    <bean id="jmsQueueTemplate" class="org.springframework.jms.core.JmsTemplate">
        <property name="connectionFactory" ref="connectionFactoty"/>
        <!--队列模式（点对点）默认-->
        <property name="pubSubDomain" value="false"/>
    </bean>

</beans>

```

ActivemqController.java 测试接口层

```java
package com.zja.controller;

import com.zja.activemq.producer.ProducerService;
import com.zja.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description Activamq 消息中间件 测试
 * @data 2019/10/31
 */
@RestController
@RequestMapping("rest/activemq")
public class ActivemqController {

    @Autowired
    private ProducerService producerService;

    //Queue 队列发送Text消息
    @GetMapping("sendQueueTextMessage")
    public void sendQueueTextMessage(){
        String message = "QueueText";
        this.producerService.sendQueueTextMessage(message);
    }
}

```



**消费者配置**

QueueTextListener.java 消费者：一定要实现监听器

```java
package com.zja.activemq.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 队列消费者：必须实现监听器,监听队列的TextMessage 消息
 * @Author: Mr.Zheng
 * @Description: 消息监听器：监听到 消息目的 有消息后自动调用onMessage(Message message)方法
 */
public class QueueTextListener implements MessageListener {

    /**
     * 接收 TextMessage消息信息，没加转换器之前接收到的是文本消息
     * @param message 文本消息（一般直接传输json字符串，所以可以认为文本消息是最通用的）
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("进入 QueueTextListener 监听器");

        TextMessage textMessage = (TextMessage) message;

        try {  //打印出消息
            System.out.println("QueueTextListener监听到消息："+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}


```

producer.xml 生产者：发送消息配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:jsm="http://www.springframework.org/schema/jms"
       xmlns:jms="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/jms
       http://www.springframework.org/schema/jms/spring-jms.xsd">

    <!--导入公共配置-->
    <import resource="common.xml"/>

    <!-- ==========================三、消费者 ：接收消息============================  -->

    <!--消息监听器注入bean中-->
    <!--queue队列监听器-->
    <bean id="queueTextListener" class="com.zja.activemq.consumer.QueueTextListener"/>

    <!-- 定义Queue监听器 : 配置多个监听器 (推荐) -->
    <jsm:listener-container destination-type="queue" connection-factory="connectionFactoty">
        <!-- TODO 每添加一个queue的监听，都需要在这里添加一个配置 -->
        <!-- 这样配置就可以方便的对多个队列监听 , 每增加一个队列只需要添加一个 jms:listener -->
        <!-- destination:队列名称, ref:指向对应的监听器对象 -->

        <!--destination 是上面配置的 队列模式_文本消息-设置Text消息队列的名字 -->
        <jsm:listener destination="queue.Text" ref="queueTextListener"/>
        
    </jsm:listener-container>

</beans>

```

spring-mvc.xml

```xml
    <!-- spring-ActiveMQ 配置分开方便查看 -->
    <import resource="classpath:jms/consumer.xml"/>
    <import resource="classpath:jms/producer.xml"/>
    
```



测试：启动项目访问

http://localhost:8080/spring5x-activemq/rest/activemq/sendQueueTextMessage

控制台的打印信息：

```python
Queue发送的TextMessage消息内容：QueueText
进入 QueueTextListener 监听器
QueueTextListener监听到消息：QueueText

```

说明Queue队列模式配置成功！！！



#### 2、ActiveMQ 的 Topic 主题模式

****

Topic 主题模式 配置和 Queue队列模式一样，一会我把完整配置在下方贴出来就能看明白了，如果不明白，可以在下方的github地址，查看项目！！！



#### 3、配置对象转换器 

****

ObjectMessageConverter.java 对象转换器，可以配置给队列或主题

```java
package com.zja.util;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;

/**
 * @author ZhengJa
 * @description 功能说明:通用的消息对象转换类
 * @data 2019/10/31
 */
public class ObjectMessageConverter implements MessageConverter {
    //把一个Java对象转换成对应的JMS Message (生产消息的时候)
    @Override
    public Message toMessage(Object object, Session session)
            throws JMSException, MessageConversionException {

        return session.createObjectMessage((Serializable) object);
    }

    //把一个JMS Message转换成对应的Java对象 (消费消息的时候)
    @Override
    public Object fromMessage(Message message) throws JMSException,
            MessageConversionException {
        ObjectMessage objMessage = (ObjectMessage) message;
        return objMessage.getObject();
    }
}


```

producer.xml 给生产者配置消息对象转换器

```xml
	<!--对象类型转换器：只发送文本消息不用配置 -->
    <bean id="objectMessageConverter" class="com.zja.util.ObjectMessageConverter"/>

	<!--queue(队列模式)：配置jmsQueueTemplate，用于发送mq消息-->
    <bean id="jmsQueueTemplate" class="org.springframework.jms.core.JmsTemplate">
        <property name="connectionFactory" ref="connectionFactoty"/>
        <!--队列模式（点对点）-->
        <property name="pubSubDomain" value="false"/>
        <!--消息对象转换器-->
        <property name="messageConverter" ref="objectMessageConverter"/>
    </bean>

```

ProducerService.java 生产者接口：消息发送 

```java
    //Queue 队列发送对象消息，需要配置对象转换器
    void sendQueueObjectMessage(UserEntity userEntity);

```

> 添加 发送对象消息

ProducerServiceImpl.java 消息发送具体操作 ，UserEntity实体类对象

```java
    /**
     * Queue 队列发送对象类型消息，需要配置对象转换器
     * @param userEntity 对象消息
     * @return void
     */
    @Override
    public void sendQueueObjectMessage(UserEntity userEntity){
        //使用JmsTemplate发送消息
        jmsQueueTemplate.send(destinationQueueObject, new MessageCreator() {
            //创键一个消息，并没有发送
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage(userEntity);
                return objectMessage;
            }
        });
        System.out.println("Queue发送的ObjectMessage消息内容："+userEntity);
    }

```

> 发送 UserEntity实体类对象 消息

QueueObjectListener.java 消费者：对象消息监听器

```java
package com.zja.activemq.consumer;

import com.zja.entity.UserEntity;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 队列消费者：必须实现监听器,监听队列的ObjectMessage 消息
 * @Author: Mr.Zheng
 * @Description: 消息监听器：监听到 消息目的 有消息后自动调用onMessage(Message message)方法
 */
public class QueueObjectListener implements MessageListener {

    //接收 ObjectMessage消息信息，加了转换器之后接收到的ObjectMessage对象消息
    @Override
    public void onMessage(Message message) {
        System.out.println("进入 QueueObjectListener 监听器");

        ObjectMessage objectMessage = (ObjectMessage) message;
        UserEntity users;
        try { //打印出消息
            users = (UserEntity) objectMessage.getObject();
            System.out.println("QueueObjectListener监听到消息：\t" + users);
            //do something ...
        } catch (JMSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}


```

ActivemqController.java

```java
    //Queue 队列发送对象消息，需要配置对象转换器
    @GetMapping("sendQueueObjectMessage")
    public void sendQueueObjectMessage(){
        this.producerService.sendQueueObjectMessage(new UserEntity(1,"QueueObject",23));
    }
    
```

> 接口测试



**到此所有配置已经完成！** ，下面时完整测试配置



pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zja</groupId>
    <artifactId>spring5x-activemq</artifactId>
    <packaging>war</packaging>

    <name>spring5x-activemq</name>

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

        <!--ActiveMQ 消息中间插件-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <!--jms连接工厂-->
        <dependency>
            <groupId>javax.jms</groupId>
            <artifactId>javax.jms-api</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-core</artifactId>
            <version>5.7.0</version>
            <exclusions>
                <exclusion>
                    <artifactId>spring-context</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-nop</artifactId>
            <version>1.7.24</version>
        </dependency>

    </dependencies>

    <build>
        <finalName>spring5x-activemq</finalName>
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

producer.xml 生产者：发送消息配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:jsm="http://www.springframework.org/schema/jms"
       xmlns:jms="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/jms
       http://www.springframework.org/schema/jms/spring-jms.xsd">

    <!--导入公共配置-->
    <import resource="common.xml"/>

    <!-- ==========================三、消费者 ：接收消息============================  -->

    <!--消息监听器注入bean中-->
    <!--queue队列监听器-->
    <bean id="queueTextListener" class="com.zja.activemq.consumer.QueueTextListener"/>
    <bean id="queueObjectListener" class="com.zja.activemq.consumer.QueueObjectListener"/>
    <!--topic主题监听器-->
    <bean id="topicTextListener" class="com.zja.activemq.consumer.TopicTextListener"/>
    <bean id="topicObjectListener" class="com.zja.activemq.consumer.TopicObjectListener"/>
    <!--公共监听器-->
    <bean id="consumerMessageListener" class="com.zja.activemq.consumer.ConsumerMessageListener"/>

    <!--配置消息监听容器 : 配置一个监听器 (不推荐)-->
    <!--<bean id="jmsCotainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
        <property name="connectionFactory" ref="connectionFactoty"/>
        &lt;!&ndash;queueDestination队列目的地，topicDestination主题目的地&ndash;&gt;
        <property name="destination" ref="topicDestination"/>
        &lt;!&ndash;消息消费者监听器&ndash;&gt;
        <property name="messageListener" ref="topicMessageListener"/>
    </bean>-->

    <!-- 定义Queue监听器 : 配置多个监听器 (推荐) -->
    <jsm:listener-container destination-type="queue" connection-factory="connectionFactoty">
        <!-- TODO 每添加一个queue的监听，都需要在这里添加一个配置 -->
        <!-- 这样配置就可以方便的对多个队列监听 , 每增加一个队列只需要添加一个 jms:listener -->
        <!-- destination:队列名称, ref:指向对应的监听器对象 -->

        <!--destination 是上面配置的 队列模式_文本消息-设置Text消息队列的名字 -->
        <jsm:listener destination="queue.Text" ref="queueTextListener"/>
        <!--destination 是上面配置的 队列模式_对象消息-设置对象消息队列的名字 -->
        <jsm:listener destination="queue.Object" ref="queueObjectListener"/>

        <!--公共监听器配置-->
        <jsm:listener destination="queue.Text" ref="consumerMessageListener"/>
    </jsm:listener-container>

    <!-- 定义Topic监听器 : 配置多个监听器 (推荐)-->
    <jsm:listener-container destination-type="topic" connection-factory="connectionFactoty">
        <!-- TODO 每添加一个topic的监听，都需要在这里添加一个配置 -->
        <!-- 这样配置就可以方便的对多个队列监听 , 每增加一个主题只需要添加一个 jms:listener -->
        <!-- destination:主题名称, ref:指向对应的监听器对象 -->

        <!--destination 是上面 配置的主题模式_文本消息-设置Text消息主题的名字 -->
        <jsm:listener destination="topic.Text" ref="topicTextListener"/>
        <!--destination 是上面 配置的主题模式_对象消息-设置对象消息主题的名字 -->
        <jsm:listener destination="topic.Object" ref="topicObjectListener"/>

        <!--公共监听器配置-->
        <jsm:listener destination="topic.Text" ref="consumerMessageListener"/>
    </jsm:listener-container>

</beans>

```

consumer.xml 消费者：接收消息，监听器配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:jsm="http://www.springframework.org/schema/jms"
       xmlns:jms="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/jms
       http://www.springframework.org/schema/jms/spring-jms.xsd">

    <!--导入公共配置-->
    <import resource="common.xml"/>

    <!-- ==========================三、消费者 ：接收消息============================  -->

    <!--消息监听器注入bean中-->
    <!--queue队列监听器-->
    <bean id="queueTextListener" class="com.zja.activemq.consumer.QueueTextListener"/>
    <bean id="queueObjectListener" class="com.zja.activemq.consumer.QueueObjectListener"/>
    <!--topic主题监听器-->
    <bean id="topicTextListener" class="com.zja.activemq.consumer.TopicTextListener"/>
    <bean id="topicObjectListener" class="com.zja.activemq.consumer.TopicObjectListener"/>
    <!--公共监听器-->
    <bean id="consumerMessageListener" class="com.zja.activemq.consumer.ConsumerMessageListener"/>

    <!--配置消息监听容器 : 配置一个监听器 (不推荐)-->
    <!--<bean id="jmsCotainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
        <property name="connectionFactory" ref="connectionFactoty"/>
        &lt;!&ndash;queueDestination队列目的地，topicDestination主题目的地&ndash;&gt;
        <property name="destination" ref="topicDestination"/>
        &lt;!&ndash;消息消费者监听器&ndash;&gt;
        <property name="messageListener" ref="topicMessageListener"/>
    </bean>-->

    <!-- 定义Queue监听器 : 配置多个监听器 (推荐) -->
    <jsm:listener-container destination-type="queue" connection-factory="connectionFactoty">
        <!-- TODO 每添加一个queue的监听，都需要在这里添加一个配置 -->
        <!-- 这样配置就可以方便的对多个队列监听 , 每增加一个队列只需要添加一个 jms:listener -->
        <!-- destination:队列名称, ref:指向对应的监听器对象 -->

        <!--destination 是上面配置的 队列模式_文本消息-设置Text消息队列的名字 -->
        <jsm:listener destination="queue.Text" ref="queueTextListener"/>
        <!--destination 是上面配置的 队列模式_对象消息-设置对象消息队列的名字 -->
        <jsm:listener destination="queue.Object" ref="queueObjectListener"/>

        <!--公共监听器配置-->
        <jsm:listener destination="queue.Text" ref="consumerMessageListener"/>
    </jsm:listener-container>

    <!-- 定义Topic监听器 : 配置多个监听器 (推荐)-->
    <jsm:listener-container destination-type="topic" connection-factory="connectionFactoty">
        <!-- TODO 每添加一个topic的监听，都需要在这里添加一个配置 -->
        <!-- 这样配置就可以方便的对多个队列监听 , 每增加一个主题只需要添加一个 jms:listener -->
        <!-- destination:主题名称, ref:指向对应的监听器对象 -->

        <!--destination 是上面 配置的主题模式_文本消息-设置Text消息主题的名字 -->
        <jsm:listener destination="topic.Text" ref="topicTextListener"/>
        <!--destination 是上面 配置的主题模式_对象消息-设置对象消息主题的名字 -->
        <jsm:listener destination="topic.Object" ref="topicObjectListener"/>

        <!--公共监听器配置-->
        <jsm:listener destination="topic.Text" ref="consumerMessageListener"/>
    </jsm:listener-container>

</beans>

```

**common.xml 最上面已经给出！！**

spring-mvc.xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc.xsd">

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

    <!-- spring-ActiveMQ 配置分开方便查看 -->
    <import resource="classpath:jms/consumer.xml"/>
    <import resource="classpath:jms/producer.xml"/>

    <!-- spring-ActiveMQ 完整配置 -->
    <!--<import resource="classpath:jms/spring-ActiveMQ.xml"/>-->

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

web.xml

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



**项目完整配置结束**



## github 地址：
* [https://github.com/zhengjiaao/spring5x](https://github.com/zhengjiaao/spring5x)


## 博客地址
* 简书：https://www.jianshu.com/u/70d69269bd09
* 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts



