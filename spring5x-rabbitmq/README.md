# spring5.x-rabbimq 高级篇

[TOC]

由于之前已经贴出一份简单RabbitMQ配置，这里不详细说配置了，直接都是全配置，代码不全的区github下拉看具体代码。

简单配置地址: [RabbitMQ 简单配置]()



spring-rabbitmq 此模块是从spring5x-base 基础模块扩展过来的

spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](spring5.x-base.md)



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、spring 集成RabbitMQ 及高级使用



建议了解：RabbitMQ 图解

https://blog.csdn.net/bestmy/article/details/84304964

https://blog.csdn.net/qq_29914837/article/details/92739464

https://www.cnblogs.com/yinfengjiujian/p/9115539.html



### 1、spring 集成RabbitMQ 及高级使用

****

common.xml   RabbitMQ 公共配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd">

    <!-- 一、 RabbitMQ 公共配置：连接 -->

    <!--扫描 rabbit 包 自动声明交换器、队列、绑定关系-->
    <!--<context:component-scan base-package="com.zja.rabbitmq.*"/>-->

    <context:property-placeholder location="classpath:rabbitmq/rabbitmq.properties" ignore-unresolvable="true"/>

    <!--1、声明连接工厂-->
    <rabbit:connection-factory id="connectionFactory"
                               addresses="${rabbitmq.addresses}"
                               username="${rabbitmq.username}"
                               password="${rabbitmq.password}"
                               virtual-host="${rabbitmq.virtual-host}"
                               publisher-returns="${rabbitmq.publisher-returns}"
                               publisher-confirms="${rabbitmq.publisher-confirms}"/>

    <!--创建一个管理器（org.springframework.amqp.rabbit.core.RabbitAdmin），用于管理交换，队列和绑定。
    auto-startup 指定是否自动声明上下文中的队列,交换和绑定, 默认值为 true。-->
    <rabbit:admin connection-factory="connectionFactory" auto-startup="true"/>

</beans>

```

producer.xml  生产者：发送消息

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd">

    <!-- 二、生产者配置：发送消息 -->

    <!--引入RabbitMQ公共配置-->
    <import resource="common.xml"/>

    <!-- **1、配置发送消息模板类** -->

    <!-- 1、rabbitTemplate 发送消息模板类：给模板指定转换器,Spring AMQP 提供了 RabbitTemplate 来简化 RabbitMQ 发送和接收消息操作-->
    <bean id="rabbitTemplate" class="org.springframework.amqp.rabbit.core.RabbitTemplate">
        <!--连接工厂-->
        <constructor-arg ref="connectionFactory"></constructor-arg>
        <!--发送方消息转换器-->
        <!--<property name="messageConverter" ref="byteMessageConverter"/>-->
        <!--消息确认回调 -->
        <property name="confirmCallback" ref="rabbitConfirmCallback"/>
        <!-- mandatory必须设置true,returnCallback才生效 -->
        <property name="returnCallback" ref="rabbitReturnCallback"/>
        <property name="mandatory" value="true"/>
    </bean>
    <!--如果消息没有到exchange,则confirm回调,ack=false -->
    <!--如果消息到达exchange,则confirm回调,ack=true -->
    <bean id="rabbitConfirmCallback" class="com.zja.rabbitmq.callback.RabbitConfirmCallback"/>
    <!--exchange到queue成功,则不回调return -->
    <!--exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了) -->
    <bean id="rabbitReturnCallback" class="com.zja.rabbitmq.callback.RabbitReturnCallback"/>
    <!-- 发送方消息转换器 -->
    <bean id="byteMessageConverter" class="com.zja.rabbitmq.converter.BytesMessageConverter"/>


    <!-- 三、队列和交换机配置 -->

    <!--**1、队列**-->

    <!--可以在 xml 采用如下方式声明交换机、队列、绑定管理 但是建议使用代码方式声明 方法更加灵活且可以采用链调用-->
    <!--定义队列queue说明：durable:是否持久化(队列持久化，就算断电队列也不会消失，但是消息会丢失)
            exclusive: 仅创建者可以使用的私有队列，断开后自动删除
            auto_delete: 当所有消费客户端连接断开后，是否自动删除队列-->
    <rabbit:queue name="queue.str1" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="queue.str2" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="queue.str3" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="queue.str4" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="queue.str5" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="queue.byte" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:queue name="queue.Object" durable="true" auto-delete="false" exclusive="false"/>


    <!-- **2、交换机** -->

    <!--fanout-exchange 广播式交换机,交换机名称 mq.fanout.exchange : 一个发送到交换机的消息都会被转发到与该交换机绑定的所有队列上-->
    <rabbit:fanout-exchange name="mq.fanout.exchange" durable="true" auto-delete="false">
        <!-- 该处把需要数据的队列与路由绑定一起，如果手动在控制台绑定就不需要此代码 -->
        <rabbit:bindings>
            <!--往名字为mq.fanout.exchange的路由里面发送数据，客户端中只要是与该路由绑定在一起的队列都会收到相关消息，
            这类似全频广播，发送端不管队列是谁，都由客户端自己去绑定，谁需要数据谁去绑定自己的处理队列-->
            <!-- 绑定队列：通过广播式交换机发送给所有队列 , 注意：不需要路由键-->
            <rabbit:binding queue="queue.str1"/>
            <rabbit:binding queue="queue.str2"/>
        </rabbit:bindings>
    </rabbit:fanout-exchange>

    <!--topic-exchange 主题交换机(常用),交换机名称 mq.topic.exchange ：发送端不只按固定的routing key发送消息，而是按字符串“匹配”发送，接收端同样如此-->
    <rabbit:topic-exchange name="mq.topic.exchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            <!--通过路由键 routing key或匹配模式 发送字符串给 队列(可以是多个)，注意：需要路由键 -->
            <rabbit:binding queue="queue.str1" pattern="routingKey.send.str.1"/>

            <!--mq.byte.send 发送字节数据(推荐) -->
            <rabbit:binding queue="queue.byte" pattern="routingKey.send.byte"/>

            <!--当路由键为str4.hello.str3 ，两个消费队列都可以收到消息;
                当路由键为str4.hello.aaa ，只有绑定了str4.#的队列才可以收到消息;
                当路由键为bbb.hello.str3 ，只有绑定了*.*.str3的队列才可收到消息-->
            <rabbit:binding queue="queue.str3" pattern="*.*.str3"/>
            <rabbit:binding queue="queue.str4" pattern="str4.#"/>

        </rabbit:bindings>
    </rabbit:topic-exchange>

    <!--direct-exchange 直连交换机,交换机名称 mq.direct.exchange : 要求该消息与一个特定的路由键完全匹配,一对一的匹配才会转发-->
    <rabbit:direct-exchange name="mq.direct.exchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            <!--direct exchange: 所有消息发送到 direct exchange 的消息被转发到 routing key 中指定的queue，注意：需要路由键-->
            <rabbit:binding queue="queue.str2" key="routingKey.send.str.2"/>
            <rabbit:binding queue="queue.str3" key="routingKey.send.str.3"/>
            <rabbit:binding queue="queue.str5" key="routingKey.send.str.5"/>
        </rabbit:bindings>
    </rabbit:direct-exchange>

    <!--headers-exchange Headers交换机(不常用，也不推荐用),交换机名称 mq.headers.exchange : 非路由键，除此之外，header 交换器和 direct 交换器完全一致，但是性能却差很多，因此基本上不会用到该交换器-->
    <!--<rabbit:headers-exchange name="mq.headers.exchange" durable="true" auto-delete="false">
        <rabbit:bindings>
            &lt;!&ndash;注意：非路由键&ndash;&gt;
            <rabbit:binding queue="queue.str4" key="routingKey.send.str.4"/>
        </rabbit:bindings>
    </rabbit:headers-exchange>-->


</beans>

```

consumer.xml  消费者：接收消息

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd">

    <!-- 三、消费者(监听器)配置：接收消息 -->

    <!-- **1、引入RabbitMQ公共配置** -->
    <import resource="common.xml"/>

    <!-- **2、队列 ：生产者已经创建队列，此处可以省略不写**-->

    <!-- **3、交换机 ：指将交换机与队列绑定在一块，也可以在rabbitMq的控制台上手动绑定** -->
    <!-- 其实发送端已经绑定过，也没必要绑定，此处可以省略不写 -->


    <!-- **4、消息接收者（必须实现监听器）** -->
    <bean id="queueStr1Consumer" class="com.zja.rabbitmq.consumers.QueueStr1Consumer"/>
    <bean id="queueStr2Consumer" class="com.zja.rabbitmq.consumers.QueueStr2Consumer"/>
    <bean id="queueStr3Consumer" class="com.zja.rabbitmq.consumers.QueueStr3Consumer"/>
    <bean id="queueStr4Consumer" class="com.zja.rabbitmq.consumers.QueueStr4Consumer"/>
    <bean id="queueStr5Consumer" class="com.zja.rabbitmq.consumers.QueueStr5Consumer"/>
    <bean id="queueByteConsumer" class="com.zja.rabbitmq.consumers.QueueByteConsumer"/>
    <bean id="queueObjectConsumer" class="com.zja.rabbitmq.consumers.QueueObjectConsumer"/>
    <bean id="receiveConfirmTestListener" class="com.zja.rabbitmq.consumers.ReceiveConfirmTestListener"/>

    <!-- **5、队列监听器：观察 监听模式 当有消息到达时会通知监听在对应的队列上的监听对象** -->
    <rabbit:listener-container connection-factory="connectionFactory">
        <rabbit:listener  queues="queue.str1"  ref="queueStr1Consumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="queue.str2"  ref="queueStr2Consumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="queue.str3"  ref="queueStr3Consumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="queue.str4"  ref="queueStr4Consumer"/>
    </rabbit:listener-container>
    <!--参数中有一个acknowledge=“manual”，是对应答机制的配置，手动应答,标识的是消息确认机制为手动的确认-->
    <rabbit:listener-container connection-factory="connectionFactory" acknowledge="manual">
        <rabbit:listener  queues="queue.str5"  ref="queueStr5Consumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory">
        <rabbit:listener queues="queue.byte" ref="queueByteConsumer"/>
    </rabbit:listener-container>
    <rabbit:listener-container connection-factory="connectionFactory" >
        <rabbit:listener  queues="queue.Object"  ref="queueObjectConsumer"/>
    </rabbit:listener-container>

</beans>

```

spring-mvc.xml 配置

```xml
	<!--RabbitMQ 拆分配置-->
    <!--生产者配置：发送消息-->
    <import resource="classpath:rabbitmq/producer.xml"/>
    <!--消费者配置：接收消息-->
    <import resource="classpath:rabbitmq/consumer.xml"/>

```



ProducerMQController.java 生产者部分代码，其余代码，去github上看

```java
/**  ========ReturnCallback 和  ConfirmCallback=========  **/

    /**ConfirmCallback ：消息到达交换机 confirm被回调，返回ack=true
     * 1、exchange,queue 都正确,confirm被回调, ack=true
     * @param
     * @return java.lang.String
     */
    @RequestMapping("confirm/sendMsg")
    public String sendAmqbMsg5() {
        String firstReceived = " 此 mq.direct.exchange 交换机存在，队列也存在 ";
        rabbitTemplate.convertAndSend("mq.direct.exchange", "routingKey.send.str.5", firstReceived);
        return "success";
    }

    /**ConfirmCallback: 消息无法到达交换机 confirm被回调，返回ack=false
     * 2、exchange 错误,queue 正确,confirm被回调, ack=false
     * @param
     * @return java.lang.String
     */
    @RequestMapping("confirm/sendMsg2")
    public String sendAmqbMsg6() {
        String firstReceived = " 此 mq.NotExchange 交换机不存在，无法到达交换机！！ ";
        rabbitTemplate.convertAndSend("mq.NotExchange", "routingKey.send.str.5", firstReceived);
        return "success";
    }

    /**ReturnCallback：消息无法从交换机到达队列，返回被监听ReturnCallback，正确到达队列，不被监听
     * 3、exchange 正确,queue 错误 ,confirm被回调, ack=true; return被回调 replyText:NO_ROUTE
     * @param
     * @return java.lang.String
     */
    @RequestMapping("return/sendMsg")
    public String sendAmqbMsg7() {
        // 返回 ReturnCallback 并被监听到
        String firstReceived = "存在 mq.topic.exchange 交换机,不存在的 No.mq.send 队列绑定的路由键，无法到达队列";
        rabbitTemplate.convertAndSend("mq.topic.exchange", "No.mq.send", firstReceived);
        return "success";
    }

    /**ReturnCallback：消息无法从交换机到达队列，返回被监听ReturnCallback，正确到达队列，不被监听
     * 4、exchange 错误,queue 错误,confirm被回调, ack=false
     * @param
     * @return java.lang.String
     */
    @RequestMapping("return/sendMsg2")
    public String sendAmqbMsg8() {
        String firstReceived = "不存在 mq.NotExchange 交换机,无法到达交换机.";
        rabbitTemplate.convertAndSend("mq.NotExchange", "No.mq.send", firstReceived);
        return "success";
    }

```

ReturnCallback 和  ConfirmCallback 测试接口，控制打印效果：

````python
进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange
消息唯一标识：null
确认结果 ack：true
失败原因：null
QueueStr5Consumer消费掉了:   此 mq.direct.exchange 交换机存在，队列也存在 
    
进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange
消息唯一标识：null
确认结果 ack：false
失败原因：channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'mq.NotExchange' in vhost '/', class-id=60, method-id=40)
 
进入 RabbitReturnCallback 回调 --> exchange到queue失败
返回消息内容 : 存在 mq.topic.exchange 交换机,不存在的 No.mq.send 队列绑定的路由键，无法到达队列
消息回复代码 : 312
描述 : NO_ROUTE
消息使用的交换器 exchange : mq.topic.exchange
消息使用的路由键 routing : No.mq.send
进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange
消息唯一标识：null
确认结果 ack：true
失败原因：null

进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange
消息唯一标识：null
确认结果 ack：false
失败原因：channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'mq.NotExchange' in vhost '/', class-id=60, method-id=40)

````



