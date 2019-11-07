package com.zja.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description RabbitMQ 配置文件: 生产者配置producer.xml，公共配置common.xml
 * @data 2019/11/4
 */
@RestController
@RequestMapping("rest/producer")
public class ProducerMQController {

    //Spring AMQP 提供了 RabbitTemplate 来简化 RabbitMQ 发送和接收消息操作,是实现AmqpTemplate接口具有amqpTemplate功能
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**       RabbitMQ 四种交换机模式      **/

    /**
     * fanout-exchange 广播式交换机: 广播，发送端不管队列是谁，都由客户端自己去绑定，谁需要数据谁去绑定自己的处理队列
     * @param
     * @return java.lang.String
     */
    @RequestMapping("fanout/sendMsg")
    public String sendAmqbMsg() {

        for(int i = 1; i <= 5; i++) {
            String allReceived = "hello" + i;
            //往名字为mq.fanout.exchange的路由里面发送数据，客户端中只要是与该路由绑定在一起的队列都会收到相关消息
            rabbitTemplate.send("mq.fanout.exchange", "", new Message(allReceived.getBytes(), new MessageProperties()));
            //rabbitTemplate.convertAndSend("mq.fanout.exchange", "", allReceived);
        }
        return "success";
    }

    /**
     * topic-exchange 主题交换机(常用): 发送端不只按固定的routing key发送消息，而是按字符串“匹配”发送，接收端同样如此
     * @param
     * @return java.lang.String
     */
    @RequestMapping("topic/sendMsg")
    public String sendAmqbMsg2() {

        for(int i = 1; i <= 5; i++) {
            String str = "hello" + i;
            //当路由键为str4.hello.str3 ，两个消费队列都可以收到消息;
            //当路由键为str4.hello.aaa ，只有绑定了str4.#的队列才可以收到消息;
            //当路由键为bbb.hello.str3 ，只有绑定了*.*.str3的队列才可收到消息
            rabbitTemplate.send("mq.topic.exchange", "str4.hello.str3", new Message(str.getBytes(), new MessageProperties()));
            //rabbitTemplate.send("mq.topic.exchange", "str4.hello.aaa", new Message(str.getBytes(), new MessageProperties()));
            //rabbitTemplate.send("mq.topic.exchange", "bbb.hello.str3", new Message(str.getBytes(), new MessageProperties()));

            //只有 queue.str2 队列能接收消息
            //rabbitTemplate.convertAndSend("mq.topic.exchange", "routingKey.send.str.1", str);
        }
        return "success";
    }

    /**
     * direct-exchange 直连交换机 : 要求该消息与一个特定的路由键完全匹配,一对一的匹配才会转发
     * @param
     * @return java.lang.String
     */
    @RequestMapping("direct/sendMsg")
    public String sendAmqbMsg3() throws InterruptedException {
        for(int i=0;i<5;i++){
            String str = "hello" + i;
            rabbitTemplate.convertAndSend("mq.direct.exchange", "routingKey.send.str.3", str);
            //rabbitTemplate.convertAndSend("mq.direct.exchange", "routingKey.send.str.4", str);
        }
        return "success";
    }

    /**
     * headers-exchange Headers交换机(不常用，也不推荐用，测试失败)： header交换器和 direct交换器完全一致，但是性能却差很多，因此基本上不会用到该交换器
     * @param
     * @return java.lang.String
     */
    /*@RequestMapping("headers/sendMsg")
    public String sendAmqbMsg4() {
        String firstReceived = "只能被接收到";
        rabbitTemplate.convertAndSend("mq.headers.exchange", "", firstReceived);
        return "success";
    }
*/

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

}

