#spring5x-rocketmq

spring5x-rocketmq此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构
如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、RocketMQ 消息中间件介绍及安装
- 2、spring集成 RocketMQ依赖和配置
- 3、RocketMQ 单元测试发送消息实例
- 4、项目完整pom
- 5、项目的github和博客地址


### 1、RocketMQ 消息中间件介绍及安装
消息中间件介绍推荐：
- https://www.jianshu.com/p/2838890f3284
- https://www.jianshu.com/p/453c6e7ff81c

有以下几点需要注意：
1.消息顺序：发送按顺序，接收按顺序
2.重复消费：rocketmq不提供解决重复消费，需要自己代码实现处理
3.分布式事务消费

消息中间件安装(Windows 安装 RocketMQ)：
- https://www.jianshu.com/p/c998dd3ea85c

### 2、spring集成 RocketMQ依赖和配置
```xml
    <properties>
        <!--junit5-->
        <junit5.version>5.1.0</junit5.version>
        <junit5-platform.version>1.1.0</junit5-platform.version>
    </properties>
    <dependencies>
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

        <!--RocketMQ 消息中间价-->
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-client</artifactId>
            <version>4.2.0</version>
        </dependency>
    </dependencies>
```
**spring集成配置：**
RocketMQ-Consumer.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--RocketMQ 消费者配置-->

    <!--pushConsumer消费者监听器-->
    <bean id="pushConsumerListener" class="com.zja.rocketmq.consumer.ConcurrentlyConsumerListenerImpl"/>
    <bean id="pushOrderlyConsumerListener" class="com.zja.rocketmq.consumer.OrderlyConsumerListenerImpl"/>


    <!--DefaultMQPushConsumer需要设置三 个 参数 : 1.这个Consumer的GroupName，2.NameServer的地址和端口号，3.是 Topic 的名称 -->
    <bean id="pushConsumer" class="org.apache.rocketmq.client.consumer.DefaultMQPushConsumer" init-method="start"
          destroy-method="shutdown">
        <!--GroupName：用于把多个 Consumer组织到一起,提高并发处理能力,与消息模式 (MessageModel)配合使用-->
        <!--Consumer 组名，参数默认值是：DEFAULT_CONSUMER，多个 Consumer如果属于一个应用，订阅同样的消息，且消费逻辑一致，则应该将它们归为同一组-->
        <property name="consumerGroup" value="concurrent_consumer"/>
        <!--NameServer的地址和端口号,可以填写多个 ，用";"分号隔开，达到消除单点故障的目的-->
        <property name="namesrvAddr" value="127.0.0.1:9876"/>
        <!--Topic名称用来标识消息类型， 需要提前创建-->
        <!--如果不需要消费某 个 Topic 下的所有消息，可以通过指定消息的 Tag 进行消息过滤，比如: Consumer.subscribe (”TopicTest”，’tag1 || tag2 || tag3”)，
            表示这个 Consumer要 消费“ TopicTest”下带有 tag1 或 tag2 或 tag3 的消息。在填写 Tag 参数的位置，用 null 或者“ *“ 表示要消费这个 Topic 的所有消息-->
        <property name="subscription">
            <map>
                <!--Topic的名称-->
                <entry key="TopicTest">
                    <!--tag 方式过滤-->
                    <!--<value>*</value>-->
                    <value>TagA</value>
                    <!--<value>TagB</value>-->
                    <!--<value>TagA || TagC || TagD</value>-->
                </entry>
            </map>
        </property>

        <!--设置消费线程数大小取值范围都是 [1, 1000]。
            4.2版本中的默认配置为：
                consumeThreadMin 默认是20
                consumeThreadMax 默认是64-->
        <!--<property name="consumeThreadMin" value="20"/>-->
        <!--<property name="consumeThreadMax" value="64"/>-->

        <!--定义消费Client从那个位置消费消息
                CONSUME_FROM_LAST_OFFSET 默认策略：从该队列最尾开始消费，即跳过历史消息
                CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
                CONSUME_FROM_TIMESTAMP 从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
        -->
        <!--<property name="consumeFromWhere" value="CONSUME_FROM_LAST_OFFSET"/>-->

        <!--RocketMQ支持两种消费模式: Clustering(集群)和Broadcasting(广播)-->
        <!-- Clustering(集群)模式: 同一个 ConsumerGroup(GroupName相同) 里的每 个 Consumer 只消费所订阅消 息的一部分 内容，
         同一个 ConsumerGroup 里所有的 Consumer消费的内容合起来才是所订阅 Topic 内容的整体， 从而达到负载均衡的目的-->
        <!-- Broadcasting(广播)模式: 同一个 ConsumerGroup里的每个 Consumer都 能消费到所订阅 Topic 的全部消息，
        也就是一个消息会被多次分发，被 多个 Consumer消费-->
        <!--<property name="messageModel" value="BROADCASTING"/>-->

        <!--分配消息队列策略，默认平均分配：AllocateMessageQueueAveragely-->
        <!--<property name="allocateMessageQueueStrategy" ref=""/>-->

        <!--批量消费最大消息条数，取值范围: [1, 1024]。默认是1-->
        <!--<property name="consumeMessageBatchMaxSize" value="1"/>-->

        <!--消费者去broker拉取消息时，一次拉取多少条。取值范围: [1, 1024].默认是32,可选配置-->
        <!--<property name="pullBatchSize" value="32"/>-->

        <!--检查拉取消息的间隔时间，由于是长轮询，所以为 0，但是如果应用为了流控，也可以设置大于 0 的值，单位毫秒，取值范围: [0, 65535]-->
        <!--<property name="pullInterval" value="0"/>-->

        <!--单队列并行消费允许的最大跨度取值范围都是 [1, 65535]，默认是2000,这个参数只在并行消费的时候才起作用-->
        <!--<property name="consumeConcurrentlyMaxSpan" value="2000"/>-->

        <!--消费者监听器配置-->
        <!--无序消息监听器-监听主题下所有队列消息-->
        <property name="messageListener" ref="pushConsumerListener"/>
        <!--顺序消息监听器-订阅有序消息-->
        <!--<property name="messageListener" ref="pushOrderlyConsumerListener"/>
        <property name="consumeFromWhere" value="CONSUME_FROM_FIRST_OFFSET"/>-->
    </bean>

    <!--DefaultMQPullConsumer:调用pull方法主动去拉取消息-->
    <bean id="pullConsumer" class="org.apache.rocketmq.client.consumer.DefaultMQPullConsumer" init-method="start"
          destroy-method="shutdown">
        <property name="consumerGroup" value="concurrent_pull_consumer_group_1"/>
        <property name="namesrvAddr" value="127.0.0.1:9876"/>

        <!--消息队列监听器-->
        <!--<property name="messageQueueListener" ref="pullConsumerListener"/>-->
    </bean>

    <!--pullConsumer消费者监听器-->
    <bean id="pullConsumerListener" class="com.zja.rocketmq.consumer.PullConsumerListenerImpl"/>

</beans>

```
RocketMQ-Producer.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!---RocketMQ 生产者配置-->

    <bean id="producer" class="org.apache.rocketmq.client.producer.DefaultMQProducer" init-method="start" destroy-method="shutdown">
        <property name="producerGroup" value="producer_group_1"/>
        <property name="namesrvAddr" value="127.0.0.1:9876"/>
        <!--回调执行-->
        <!--<property name="callbackExecutor" ref=""/>-->
         <!--发送失败时重试次数-->
        <!--<property name="retryTimesWhenSendFailed" value=""/>-->
        <!--发送异步失败时重试次数-->
        <!--<property name="retryTimesWhenSendAsyncFailed" value=""/>-->
    </bean>

<!--    <bean id="transactionMQProducer" class="org.apache.rocketmq.client.producer.TransactionMQProducer" init-method="start" destroy-method="shutdown">-->
<!--        <property name="producerGroup" value="producer_group_2"/>-->
<!--        <property name="namesrvAddr" value="127.0.0.1:9876"/>-->
<!--        &lt;!&ndash;回调执行&ndash;&gt;-->
<!--        &lt;!&ndash;<property name="callbackExecutor" ref=""/>&ndash;&gt;-->
<!--        &lt;!&ndash;发送失败时重试次数&ndash;&gt;-->
<!--        &lt;!&ndash;<property name="retryTimesWhenSendFailed" value=""/>&ndash;&gt;-->
<!--        &lt;!&ndash;发送异步失败时重试次数&ndash;&gt;-->
<!--        &lt;!&ndash;<property name="retryTimesWhenSendAsyncFailed" value=""/>&ndash;&gt;-->

<!--        &lt;!&ndash;事务检查侦听器&ndash;&gt;-->
<!--        &lt;!&ndash;<property name="transactionCheckListener" ref=""/>&ndash;&gt;-->
<!--    </bean>-->

</beans>

```


### 3、RocketMQ 单元测试发送消息实例
##### 使用配置文件测试
**demo1**
```java
package dome1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Date: 2019-12-06 15:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：通过JUnit先运行消费者,再运行生产者,在消费者的控制台中能看到生产者发送的消息已经打印出来
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath*:META-INF/spring/rocketmq/RocketMQ-Consumer.xml"})
public class JUnitConsumer {

    /**
     * 通过JUnit先运行消费者(dome1.JUnitConsumer),再运行生产者(dome1.JUnitProducer),在消费者的控制台中能看到生产者发送的消息已经打印出来
     * 当然，先运行生产者，再运行消费者，消费者运行后也可以获取到消息。
     */
    @Test
    public void runConsumer() {
        System.out.println("Consumer Started.");

        // 下面的代码把线程阻塞住,这样就可以先运行消费者再运行生产者.当然不要也可以,不要的化就得先运行生产者,
        //再运行消费者,生产者先把消息发送到MQ上,消费者启动后从MQ上拿消息
        synchronized (JUnitConsumer.class) {
            while (true) {
                try {
                    JUnitConsumer.class.wait();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

```
```java
package dome1;

import com.zja.rocketmq.producer.ProducerService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：发送消息
 */
@RunWith(JUnitPlatform.class)  // org.junit.platform.runner.JUnitPlatform
@ExtendWith(SpringExtension.class)  // org.springframework.test.context.junit.jupiter.SpringExtension
@ContextConfiguration({"classpath*:META-INF/spring/rocketmq/RocketMQ-Producer.xml"})
public class JUnitProducer {

    @Autowired
    private DefaultMQProducer producer;

    @Test
    public void producerData() throws InterruptedException {
        for (int i = 0; i < 10; i++) {  // 发10条消息
            try {
                Message msg = new Message("TopicTest", // topic
                        "TagA", // tag
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)// body
                );

                // 调用producer的send()方法发送消息
                // 这里调用的是同步的方式，所以会有返回结果
                SendResult sendResult = producer.send(msg);

                // 打印返回结果，可以看到消息发送的状态以及一些相关信息
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
    }

}

```

#####消息顺序
- 为了测试方便，以下测试不使用配置发送消息

**demo2**
消息发送者-有序
```java
package dome2;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消息发送者
 */
public class JUnitProducer {

    @Test
    public void producerData(){
        try {
            String gruop_name="orderly_producer";
            MQProducer producer = new DefaultMQProducer(gruop_name);
            ((DefaultMQProducer) producer).setNamesrvAddr("127.0.0.1:9876");
            producer.start();

            //顺序发送10条编号为0到9的，orderId为1 的消息
            new Thread(() -> {
                Integer orderId = 1;
                sendMessage(producer, orderId);
            }).start();
            //顺序发送10条编号为0到9的，orderId为2 的消息
            new Thread(() -> {
                Integer orderId = 2;
                sendMessage(producer, orderId);
            }).start();
            //顺序发送10条编号为0到9的，orderId为3 的消息
            new Thread(() -> {
                Integer orderId = 3;
                sendMessage(producer, orderId);
            }).start();
            //sleep 30秒让消息都发送成功再关闭
            Thread.sleep(1000*20);

            producer.shutdown();
        } catch (InterruptedException | MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     * @param producer 生产者
     * @param orderId 队列下标
     */
    private static void sendMessage(MQProducer producer, Integer orderId) {
        for (int i = 0; i < 5; i++) {
            try {
                //topic,tags,keys,body
                Message msg = new Message("TopicOrderly", "TagA", "KEY"+i,
                                ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据： 如果使用顺序消费，则必须自己实现MessageQueueSelector，保证消息进入同一个队列。
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId); //orderId 是队列下标
                System.out.println("message send,orderId: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            try {
                //topic,tags,keys,body
                Message msg = new Message("TopicOrderly", "TagB", "KEY"+i,
                        ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据： 如果使用顺序消费，则必须自己实现MessageQueueSelector，保证消息进入同一个队列。
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId); //orderId 是队列下标
                System.out.println("message send,orderId: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            try {
                //topic,tags,keys,body
                Message msg = new Message("TopicOrderly", "TagC", "KEY"+i,
                        ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据： 如果使用顺序消费，则必须自己实现MessageQueueSelector，保证消息进入同一个队列。
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId); //orderId 是队列下标
                System.out.println("message send,orderId: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

```
消息接收者1-接收无序(多个线程监听一个队列)
```java
package dome2;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Date: 2019-12-06 16:28
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 同一个orderId下，编号为10的消息先于编号为9的消息被消费，不是正确的顺序消费，即普通的并行消息消费，无法保证消息消费的顺序性
 */
public class Consumer1 {

    /**
     * 消费者1-消息无序消费
     * 并发消费的消费速度要比有序消费更快
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String group_name="Concurrently_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);

        consumer.setNamesrvAddr("127.0.0.1:9876");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("TopicOrderly", "*");
        //单个消费者中多线程并行消费
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMin(64);

        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("Consumer1 Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                System.out.println("ThreadName: "+Thread.currentThread().getName()+" ,queueId: "+msg.getQueueId() +" ,i: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}

```
消息接收者2-有序接收(一个线程监听一个队列)
```java
package dome2;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2019-12-06 16:29
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 理想情况下消息顺序消费的结果应该是，同一个orderId下的消息的编号i值应该顺序递增，但是不同orderId之间的消费可以并行，即局部有序即可
 */
public class Consumer2 {

    /**
     * 消费者2-消息有序消费
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Consumer2 consumer2 = new Consumer2();
    }

    public Consumer2() throws Exception{
        String group_name="orderly_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);
        consumer.setNamesrvAddr("127.0.0.1:9876");

        /**
         * 设置消费者第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //订阅主题，以及过滤的标签内容
        consumer.subscribe("TopicOrderly", "*");
        //消费者并行消费的线程
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMin(64);
        //注册监听器
        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("Consumer2 Started.");
    }

    //MessageListenerOrderly 用一个线程去监听一个队列
    static class Listener implements MessageListenerOrderly{
        //随机
        private Random random = new Random();

        @SneakyThrows
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            //设置自动提交
            context.setAutoCommit(true);

            for (MessageExt msg : msgs) {
                System.out.println("ThreadName: "+Thread.currentThread().getName()+" ,queueId: "+msg.getQueueId() +" ,i: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            try {
                //模拟业务处理中......
                TimeUnit.SECONDS.sleep(random.nextInt(5));
            }catch (Exception e){
                e.printStackTrace();
                //return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}

```
消息接收者3-有序接收(一个线程监听一个队列)
```java
package dome2;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2019-12-06 16:29
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 理想情况下消息顺序消费的结果应该是，同一个orderId下的消息的编号i值应该顺序递增，但是不同orderId之间的消费可以并行，即局部有序即可
 */
public class Consumer3 {

    /**
     * 消费者2-消息有序消费
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Consumer3 consumer3 = new Consumer3();
    }

    public Consumer3() throws Exception{
        String group_name="orderly_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);
        consumer.setNamesrvAddr("127.0.0.1:9876");

        /**
         * 设置消费者第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //订阅主题，以及过滤的标签内容
        consumer.subscribe("TopicOrderly", "*");
        //消费者并行消费的线程
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMin(64);
        //注册监听器
        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("Consumer3 Started.");
    }

    //MessageListenerOrderly 用一个线程去监听一个队列
    static class Listener implements MessageListenerOrderly{
        //随机
        private Random random = new Random();

        @SneakyThrows
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            //设置自动提交
            context.setAutoCommit(true);

            for (MessageExt msg : msgs) {
                System.out.println("ThreadName: "+Thread.currentThread().getName()+" ,queueId: "+msg.getQueueId() +" ,i: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            try {
                //模拟业务处理中......
                TimeUnit.SECONDS.sleep(random.nextInt(5));
            }catch (Exception e){
                e.printStackTrace();
                //return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}

```

##### 消息重试机制
- 消费端没有消费成功或有异常时，重发消息
**demo3**
消息发送者
```java
package dome3;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：消息发送
 */
public class JUnitProducer {

    @Test
    public void producerData2() throws Exception {

        //生产者组名
        String producerGroup="test_producer";

        //使用生产者组名称实例化
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //设置发送失败时的重试时间10s
        producer.setRetryTimesWhenSendFailed(10);
        //指定名称服务器地址
        producer.setNamesrvAddr("localhost:9876");
        //启动实例
        producer.start();
        //发送20条数据，模拟消息实体内容为5的这条消息在消费端消费失败(抛异常，MQ进行消息重发操作)
        //发送一条数据，消费端抛异常重试3次
        for (int i = 0; i < 1; i++) {
            //创建一个消息实例，指定主题topic、标记tag和消息主体body
            Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //调用发送消息将消息传递给代理之一。
            SendResult sendResult = producer.send(msg);
            System.out.println("sendResult: "+sendResult);
        }
        //一旦生产者实例不再使用，就立即关闭。
        producer.shutdown();
    }

}

```
消息接收者
```java
package dome3;

import dome2.Consumer1;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Date: 2019-12-06 15:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消费者：消息接收
 */
public class JUnitConsumer {

    /**
     * 测试消费异常 重发机制 3次
     */
    public static void main(String[] args) throws Exception {
        //消费者组名
        String consumerGroup = "test_consumer_group";

        //使用指定的使用者组名称实例化
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        //设置消耗线程最小值/最大值
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(64);
        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");
        //消费者订阅主题
        consumer.subscribe("TopicTest", "*");
        //消费者监听器
        consumer.registerMessageListener(new Listener());
        //启动消费者实例
        consumer.start();
        System.out.println("JUnitConsumer Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            //重试机制-测试,发送一条消息,抛异常重试3次
            MessageExt msg = msgs.get(0);
            try {
                String topic = msg.getTopic();
                String msgBody = new String(msg.getBody(), "utf-8");
                String tags = msg.getTags();
                //属性原始id，方便去重
                String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                System.out.println("orignMsgId：" + orignMsgId + " ,topic:" + topic + " ,tags:" + tags + " ,msgBody:" + msgBody);
                //TimeUnit.SECONDS.sleep(20);
                int a=1/0;  //抛异常测试重试次数
            } catch (Exception e) {
                e.printStackTrace();
                //允许重发3次,重试3次后结束并记录到日志
                if (msg.getReconsumeTimes() == 3) {
                    System.err.println("---------记录日志---------");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

}

```
##### 消息发送(同步、异步、单向)
- 消息发送者以同步、异步、单向等方式发送消息
**demo4**
消息发送者
```java
package dome4;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;

/**
 * Date: 2019-12-09 15:56
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者
 */
public class JunitProducer {

    /**
     * 1.同步发送消息
     * 在重要的通知消息，SMS通知，SMS营销系统等广泛的场景中使用可靠的同步传输。
     */
    @Test
    public void test() throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new
                DefaultMQProducer("please_rename_unique_group_name");
        // Specify name server addresses.
        producer.setNamesrvAddr("localhost:9876");
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 20; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    "keys"+i,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }

    /**
     * 2.异步发送消息
     * 异步传输通常用于对时间敏感的业务场景中
     */
    @Test
    public void test2() throws Exception{
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        // Specify name server addresses.
        producer.setNamesrvAddr("localhost:9876");
        //Launch the instance.
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);
        for (int i = 0; i < 20; i++) {
            final int index = i;
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest",
                    "TagA",
                    "OrderID "+i,
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index,
                            sendResult.getMsgId());
                }
                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }

    /**
     * 3.以单向模式发送消息
     * 单向传输用于要求中等可靠性的情况，例如日志收集
     */
    @Test
    public void test3() throws Exception{
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        // Specify name server addresses.
        producer.setNamesrvAddr("localhost:9876");
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 20; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            producer.sendOneway(msg);

        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}

```
消息接收者
```java
package dome4;

import dome3.JUnitConsumer;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Date: 2019-12-09 16:06
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public class JunitConsumer {

    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        //消费者组名
        String consumerGroup = "test_consumer_group";

        //使用指定的使用者组名称实例化
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        //设置消耗线程最小值/最大值
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(64);
        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");
        //消费者订阅主题
        consumer.subscribe("TopicTest", "*");
        //消费者监听器
        consumer.registerMessageListener(new Listener());
        //启动消费者实例
        consumer.start();
        System.out.println("JUnitConsumer Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                System.out.println("ThreadId: "+Thread.currentThread().getId()+" ,queueId: "+msg.getQueueId() +" ,Keys: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}

```
由于demo*代码过多，可以参考github地址，查询项目代码，不再一一贴出来了
大致有以下测试：
**demo5:** 订购实例
**demo6:** 广播实例
**demo7:** 预定消息
**demo8:** 消息过滤
**demo9:** 批处理发送消息
**demo10:** 消息事务-未成功
**demo11:** 手动pull拉取指定队列上的消息



### 4、项目完整pom
pom.xml
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
    <artifactId>spring5x-rocketmq</artifactId>
    <packaging>war</packaging>

    <name>spring5x-rocketmq</name>

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

        <!--RocketMQ 消息中间价-->
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-client</artifactId>
            <version>4.2.0</version>
        </dependency>

    </dependencies>

    <build>
        <finalName>spring5x-rocketmq</finalName>
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
