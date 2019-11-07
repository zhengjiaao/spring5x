# spring5.x-rabbitmq

[TOC]

spring-rabbitmq 此模块是从spring5x-base 基础模块扩展过来的

spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](spring5.x-base.md)



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、spring 集成RabbitMQ 及使用
- 2、ConfirmCallback的使用及触发的一种场景
- 3、ReturnCallback的使用及触发的一种场景



建议了解：RabbitMQ 图解

https://blog.csdn.net/bestmy/article/details/84304964

https://blog.csdn.net/qq_29914837/article/details/92739464

https://www.cnblogs.com/yinfengjiujian/p/9115539.html



### 1、spring 集成RabbitMQ 及使用

****

pom.xml

```xml
		<!--spring rabbitmq 整合依赖-->
        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit</artifactId>
            <version>2.1.10.RELEASE</version>
        </dependency>
        <!--rabbitmq 传输对象序列化依赖了这个包-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.8</version>
        </dependency>

```

> 注：也可以这样单独引用，spring-rabbit 内部包含了好多模块

rabbitmq.properties

```properties
#默认java连接端口
rabbitmq.addresses=localhost:5672
rabbitmq.username=guest
rabbitmq.password=guest
# 虚拟主机，等价于名称空间，默认为 / ，如果想使用其他名称空间必须先用图形界面或者管控台添加，程序不会自动创建
rabbitmq.virtual-host=/

```

spring-RabbitMQ.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd">

    <!--扫描 rabbit 包 自动声明交换器、队列、绑定关系，启动了springmvc注解扫描这里可以注释掉-->
    <!--<context:component-scan base-package="com.zja.config"/>-->

    <!--引入rabbitmq.properties配置-->
    <context:property-placeholder location="classpath:rabbitmq.properties" ignore-unresolvable="true"/>

    <!--声明连接工厂-->
    <rabbit:connection-factory id="connectionFactory"
                               addresses="${rabbitmq.addresses}"
                               username="${rabbitmq.username}"
                               password="${rabbitmq.password}"
                               virtual-host="${rabbitmq.virtual-host}" />

    <!--创建一个管理器（org.springframework.amqp.rabbit.core.RabbitAdmin），用于管理交换，队列和绑定。
    auto-startup 指定是否自动声明上下文中的队列,交换和绑定, 默认值为 true。-->
    <rabbit:admin connection-factory="connectionFactory" auto-startup="true"/>

    <!--声明 template 的时候需要声明 id 不然会抛出异常-->
    <rabbit:template id="rabbitTemplate" connection-factory="connectionFactory"/>

    <!--可以在 xml 采用如下方式声明交换机、队列、绑定管理 但是建议使用代码方式声明 方法更加灵活且可以采用链调用-->
    <!--定义queue  说明：durable:是否持久化 exclusive: 仅创建者可以使用的私有队列，断开后自动删除 auto_delete: 当所有消费客户端连接断开后，是否自动删除队列-->
    <rabbit:queue name="mq.queue1" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="mq.queue2" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="mq.remoting" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="mq.byte" durable="true" auto-delete="false" exclusive="false"/>

    <!--定义 主题 topic-exchange 交换机 路由键 mq.queueExchange -->
    <rabbit:topic-exchange name="mq.topicExchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            <!--mq.queueAll.send 发送字符串给所有队列 -->
            <rabbit:binding queue="mq.queue1" pattern="mq.queueAll.send"/>
            <rabbit:binding queue="mq.queue2" pattern="mq.queueAll.send"/>

            <!--mq.queue2.send 发送字符串给mq.queue2队列 -->
            <rabbit:binding queue="mq.queue2" pattern="mq.queue2.send"/>
            <!--mq.byte.send 发送字节数据(推荐) -->
            <rabbit:binding queue="mq.byte" pattern="mq.byte.send"/>
        </rabbit:bindings>
    </rabbit:topic-exchange>

    <!--定义direct-exchange 交换机  路由键 mq.remotingExchange -->
    <rabbit:direct-exchange name="mq.directExchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            <!--mq.remoting.send 发送字符串给mq.remoting队列 -->
            <rabbit:binding queue="mq.remoting" key="mq.remoting.send"/>
        </rabbit:bindings>
    </rabbit:direct-exchange>

    <!-- 消息接收者 -->
    <bean id="queue1Consumer" class="com.zja.rabbitmq.consumers.Queue1Consumer"/>
    <bean id="queue2Consumer" class="com.zja.rabbitmq.consumers.Queue2Consumer"/>
    <bean id="remotingConsumer" class="com.zja.rabbitmq.consumers.RemotingConsumer"/>
    <bean id="byteConsumer" class="com.zja.rabbitmq.consumers.ByteConsumer"/>

    <!-- queue litener 观察 监听模式 当有消息到达时会通知监听在对应的队列上的监听对象 -->
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="mq.queue1"  ref="queue1Consumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="mq.queue2"  ref="queue2Consumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="mq.remoting"  ref="remotingConsumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory">
        <rabbit:listener queues="mq.byte" ref="byteConsumer"/>
    </rabbit:listener-container>

</beans>

```



**消费者(接收消息)：**

Queue1Consumer.java

```java
package com.zja.rabbitmq.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description 消费者1
 * @data 2019/11/4
 */
public class Queue1Consumer implements MessageListener {

    /**
     * 消费者接收消息
     * @param message 推荐使用字节
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("进入Queue1Consumer 的监听器");

        MessageProperties m=message.getMessageProperties();
        //System.out.println("m "+m);
        String msg= null;
        try {
            //utf-8 解决 消费者接收中文消息乱码
            msg = new String (message.getBody(),"utf-8");
            System.out.println("Queue1Consumer消费掉了:  "+msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

```

Queue2Consumer.java

```java
package com.zja.rabbitmq.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description 消费者2
 * @data 2019/11/4
 */
public class Queue2Consumer implements MessageListener {

    /**
     * 消费者接收消息
     * @param message 推荐使用字节
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("进入Queue2Consumer 的监听器");
        MessageProperties m=message.getMessageProperties();
        //System.out.println("m "+m);
        String msg= null;
        try {
            //utf-8 解决 消费者接收中文消息乱码
            msg = new String (message.getBody(),"utf-8");
            System.out.println("Queue2Consumer消费掉了:  "+msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

```

RemotingConsumer.java

```java
package com.zja.rabbitmq.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description 消费者
 * @data 2019/11/4
 */
public class RemotingConsumer implements MessageListener {

    /**
     * 消费者接收消息
     * @param message 推荐使用字节
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("进入RemotingConsumer 监听器");
        MessageProperties m=message.getMessageProperties();
        //System.out.println("m "+m);
        String msg= null;
        try {
            //utf-8 解决 消费者接收中文消息乱码
            msg = new String (message.getBody(),"utf-8");
            System.out.println("RemotingConsumer消费掉了:  "+msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

```

ByteConsumer.java

```java
package com.zja.rabbitmq.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description 消费者接收字节数据
 * @data 2019/11/4
 */
public class ByteConsumer implements MessageListener {

    /**
     * 消费者接收消息
     * @param message 推荐使用字节
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("进入ByteConsumer 的监听器");

        MessageProperties m=message.getMessageProperties();

        byte[] body = message.getBody();
        try {
            System.out.println("ByteConsumer消费掉了:  "+new String(body,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

```

RabbitMQController.java 测试接口

```java
package com.zja.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description RabbitMQ 测试
 * @data 2019/11/4
 */
@RestController
@RequestMapping("rest/rabbit")
public class RabbitMQController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMsg")
    @ResponseBody
    public String sendAmqbMsg() {
        String allReceived = "我的路由键 mq.topicExchange 符合 mq.queue1、mq.queue2和mq.remoting 的要求，我应该被三个个监听器接收到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.queueAll.send", allReceived);
        return "success";
    }

    @RequestMapping("/sendMsg2")
    @ResponseBody
    public String sendAmqbMsg2() {
        String firstReceived = "我的路由键 mq.topicExchange 只符合 mq.queue2 的要求，只能被 mq.queue2 接收到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.queue2.send", firstReceived);
        return "success";
    }

    @RequestMapping("/sendMsg3")
    @ResponseBody
    public String sendAmqbMsg3() {
        String firstReceived = "我的路由键 mq.directExchange 只符合 mq.remoting 的要求，只能被 mq.remoting 接收到";
        rabbitTemplate.convertAndSend("mq.directExchange", "mq.remoting.send", firstReceived);
        return "success";
    }

    @RequestMapping("/sendMsg4")
    @ResponseBody
    public String sendAmqbMsg4() {
        String firstReceived = "我的路由键 mq.topicExchange 只符合 mq.byte 的要求，只能被 mq.byte 接收到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.byte.send", firstReceived);
        return "success";
    }
}

```

> 浏览器调用接口，查看控制台打印信息。



### 2、ConfirmCallback的使用及触发的一种场景

****

目前回调存在ConfirmCallback和ReturnCallback两者。他们的区别在于：

> 1、如果消息没有到exchange,则ConfirmCallback回调,ack=false
> 2、如果消息到达exchange,则ConfirmCallback回调,ack=true
> 3、exchange到queue成功,则不回调ReturnCallback



rabbitmq.properties

```properties
# 开启发送确认
#消息发送到交换机确认机制,是否确认回调
#如果没有本条配置信息，当消费者收到生产者发送的消息后，生产者无法收到确认成功的回调信息
rabbitmq.publisher-confirms=true

```

>rabbitmq.properties 添加 开启回调机制

spring-RabbitMQ.xml

```xml
	<!--声明连接工厂-->
    <rabbit:connection-factory id="connectionFactory"
                               addresses="${rabbitmq.addresses}"
                               username="${rabbitmq.username}"
                               password="${rabbitmq.password}"
                               virtual-host="${rabbitmq.virtual-host}"
                               publisher-confirms="${rabbitmq.publisher-confirms}"/>


	<!--声明 template 的时候需要声明 id 不然会抛出异常-->
    <!--<rabbit:template id="rabbitTemplate" connection-factory="connectionFactory"/>-->

    <!-- rabbitTemplate 消息模板类：给模板指定转换器 -->
    <bean id="rabbitTemplate" class="org.springframework.amqp.rabbit.core.RabbitTemplate">
        <!--连接工厂-->
        <constructor-arg ref="connectionFactory"></constructor-arg>
        <!--消息确认回调 -->
        <property name="confirmCallback" ref="rabbitConfirmCallback"/>
    </bean>
    <!--如果消息没有到exchange,则confirm回调,ack=false -->
    <!--如果消息到达exchange,则confirm回调,ack=true -->
    <bean id="rabbitConfirmCallback" class="com.zja.rabbitmq.callback.RabbitConfirmCallback"/>

	<!--可以在 xml 采用如下方式声明交换机、队列、绑定管理 但是建议使用代码方式声明 方法更加灵活且可以采用链调用-->
    <!--定义queue  说明：durable:是否持久化 exclusive: 仅创建者可以使用的私有队列，断开后自动删除 auto_delete: 当所有消费客户端连接断开后，是否自动删除队列-->
    <rabbit:queue name="mq.queue1" durable="true" auto-delete="false" exclusive="false"/>

	<!--定义 主题 topic-exchange 交换机 路由键 mq.queueExchange -->
    <rabbit:topic-exchange name="mq.topicExchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            <!--mq.queueAll.send 发送字符串给所有队列 -->
            <rabbit:binding queue="mq.queue1" pattern="mq.queueAll.send"/>
        </rabbit:bindings>
    </rabbit:topic-exchange>

```

>1、添加属性： publisher-confirms
>
>2、注释掉之前的rabbitTemplate写法，重构并添加消息回调 confirmCallback 属性
>
>3、添加 自定义 RabbitConfirmCallback  回调处理

RabbitConfirmCallback.java

```java
package com.zja.rabbitmq.callback;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author ZhengJa
 * @description 确认后回调
 * @data 2019/11/4
 */
public class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback {
    
    /**如果消息没有到exchange,则confirm回调,ack=false
     * 如果消息到达exchange,则confirm回调,ack=true
     * @param correlationData 消息唯一标识
     * @param ack 确认结果
     * @param cause 失败原因
     * @return void 
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息唯一标识："+correlationData);
        System.out.println("确认结果："+ack);
        System.out.println("失败原因："+cause);
    }
}

```

RabbitMQController.java

```java
package com.zja.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description RabbitMQ 测试
 * @data 2019/11/4
 */
@RestController
@RequestMapping("rest/rabbit")
public class RabbitMQController {

    //Spring AMQP 提供了 RabbitTemplate 来简化 RabbitMQ 发送和接收消息操作,是实现AmqpTemplate接口具有amqpTemplate功能
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //测试 ConfirmCallback 失败
    @RequestMapping("/sendMsg5")
    public String sendAmqbMsg5() {
        String firstReceived = "路由键不存在 mq.NotExchange ";
        rabbitTemplate.convertAndSend("mq.NotExchange", "mq.queueAll.send", firstReceived);
        return "success";
    }
    
    //测试 ConfirmCallback 成功
    @RequestMapping("/sendMsg6")
    public String sendAmqbMsg6() {
        String firstReceived = "路由键存在 mq.topicExchange  ";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.queueAll.send", firstReceived);
        return "success";
    }

}


```



失败返回返回效果：

```python
进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange
消息唯一标识：null
确认结果 ack：false
失败原因：channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'mq.NotExchange' in vhost '/', class-id=60, method-id=40)

```

成功返回效果：

```python
进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange
消息唯一标识：null
确认结果 ack：true
失败原因：null

```





### 3、ReturnCallback的使用及触发的一种场景

****

目前回调存在ConfirmCallback和ReturnCallback两者。他们的区别在于：

> 1、如果消息没有到exchange,则ConfirmCallback回调,ack=false
> 2、如果消息到达exchange,则ConfirmCallback回调,ack=true
> 3、exchange到queue成功,则不回调ReturnCallback



rabbitmq.properties

```properties
# 开启发送失败退回
#消息成功则不返回，启动消息失败返回：比如路由不到队列时触发回调
rabbitmq.publisher-returns=true

```

spring-RabbitMQ.xml

```xml
    <!--声明连接工厂-->
    <rabbit:connection-factory id="connectionFactory"
                               addresses="${rabbitmq.addresses}"
                               username="${rabbitmq.username}"
                               password="${rabbitmq.password}"
                               virtual-host="${rabbitmq.virtual-host}"
                               publisher-returns="${rabbitmq.publisher-returns}"/>

    <!--创建一个管理器（org.springframework.amqp.rabbit.core.RabbitAdmin），用于管理交换，队列和绑定。
    auto-startup 指定是否自动声明上下文中的队列,交换和绑定, 默认值为 true。-->
    <rabbit:admin connection-factory="connectionFactory" auto-startup="true"/>

    <!--声明 template 的时候需要声明 id 不然会抛出异常-->
    <!--<rabbit:template id="rabbitTemplate" connection-factory="connectionFactory"/>-->

    <!-- rabbitTemplate 消息模板类：给模板指定转换器 -->
    <bean id="rabbitTemplate" class="org.springframework.amqp.rabbit.core.RabbitTemplate">
        <!--连接工厂-->
        <constructor-arg ref="connectionFactory"></constructor-arg>
        <!-- mandatory必须设置true,returnCallback才生效 -->
        <property name="returnCallback" ref="rabbitReturnCallback"/>
        <property name="mandatory" value="true"/>
    </bean>
    <!--exchange到queue成功,则不回调return -->
    <!--exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了) -->
    <bean id="rabbitReturnCallback" class="com.zja.rabbitmq.callback.RabbitReturnCallback"/>


<!--可以在 xml 采用如下方式声明交换机、队列、绑定管理 但是建议使用代码方式声明 方法更加灵活且可以采用链调用-->
    <!--定义queue  说明：durable:是否持久化 exclusive: 仅创建者可以使用的私有队列，断开后自动删除 auto_delete: 当所有消费客户端连接断开后，是否自动删除队列-->
    <rabbit:queue name="mq.queue1" durable="true" auto-delete="false" exclusive="false"/>

	<!--定义 主题 topic-exchange 交换机 路由键 mq.queueExchange -->
    <rabbit:topic-exchange name="mq.topicExchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            <!--mq.queueAll.send 发送字符串给所有队列 -->
            <rabbit:binding queue="mq.queue1" pattern="mq.queueAll.send"/>
        </rabbit:bindings>
    </rabbit:topic-exchange>

```

> 1、添加：publisher-returns 属性
>
> 2、rabbitTemplate 新增属性returnCallback、mandatory必须设置true,return callback才生效
>
> 3、添加 RabbitReturnCallback 回调后的监听器

RabbitReturnCallback.java

```java
package com.zja.rabbitmq.callback;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author ZhengJa
 * @description 失败后回调
 * @data 2019/11/4
 */
public class RabbitReturnCallback implements RabbitTemplate.ReturnCallback{
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println(" ==== >> 进入 RabbitReturnCallback 回调 : ");
        System.out.println("消息主体 message : "+message);
        System.out.println("消息主体 message : "+replyCode);
        System.out.println("描述："+replyText);
        System.out.println("消息使用的交换器 exchange : "+exchange);
        System.out.println("消息使用的路由键 routing : "+routingKey);
    }
}

```

RabbitMQController.java

```java
package com.zja.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description RabbitMQ 测试
 * @data 2019/11/4
 */
@RestController
@RequestMapping("rest/rabbit")
public class RabbitMQController {

    //Spring AMQP 提供了 RabbitTemplate 来简化 RabbitMQ 发送和接收消息操作,是实现AmqpTemplate接口具有amqpTemplate功能
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //测试 ReturnCallback
    @RequestMapping("/sendMsg7")
    public String sendAmqbMsg7() {
        String firstReceived = "路由键存在 mq.topicExchange 发送给 不存在的 No.mq.send 返回 ReturnCallback 并被监听到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "No.mq.send", firstReceived);
        return "success";
    }
}


```

返回效果：

```python
进入 RabbitReturnCallback 回调 --> exchange到queue失败
返回消息内容 : 路由键存在 mq.topicExchange 发送给 不存在的 No.mq.send 返回 ReturnCallback 并被监听到
消息回复代码 : 312
描述 : NO_ROUTE
消息使用的交换器 exchange : mq.topicExchange
消息使用的路由键 routing : No.mq.send

```





## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)



## 博客地址

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts





