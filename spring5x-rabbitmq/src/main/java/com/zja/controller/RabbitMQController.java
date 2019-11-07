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

    @Autowired
    private AmqpTemplate amqpTemplate;


    /** 基础测试 **/

    @RequestMapping("/sendMsg")
    public String sendAmqbMsg() {
        String allReceived = "我的路由键 mq.topicExchange 符合 mq.queue1、mq.queue2和mq.remoting 的要求，我应该被三个个监听器接收到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.queueAll.send", allReceived);
        return "success";
    }

    @RequestMapping("/sendMsg2")
    public String sendAmqbMsg2() throws InterruptedException, UnsupportedEncodingException {
        String firstReceived = "我的路由键 mq.topicExchange 只符合 mq.queue2 的要求，只能被 mq.queue2 接收到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.queue2.send", firstReceived);
        return "success";
    }

    @RequestMapping("/sendMsg3")
    public String sendAmqbMsg3() throws InterruptedException {
        String firstReceived = "我的路由键 mq.directExchange 只符合 mq.remoting 的要求，只能被 mq.remoting 接收到";
        rabbitTemplate.convertAndSend("mq.directExchange", "mq.remoting.send", firstReceived);
        return "success";
    }

    @RequestMapping("/sendMsg4")
    public String sendAmqbMsg4() {
        String firstReceived = "我的路由键 mq.topicExchange 只符合 mq.byte 的要求，只能被 mq.byte 接收到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "mq.byte.send", firstReceived);
        return "success";
    }



    /**  ReturnCallback 和  ConfirmCallback  **/

    //测试 ConfirmCallback
    @RequestMapping("/sendMsg5")
    public String sendAmqbMsg5() {
        String firstReceived = "路由键不存在 mq.NotExchange ";
        rabbitTemplate.convertAndSend("mq.NotExchange", "mq.byte.send", firstReceived);
        return "success";
    }

    //测试 ReturnCallback
    @RequestMapping("/sendMsg6")
    public String sendAmqbMsg6() {
        String firstReceived = "路由键存在 mq.topicExchange 发送给 不存在的 No.mq.send 返回 ReturnCallback 并被监听到";
        rabbitTemplate.convertAndSend("mq.topicExchange", "No.mq.send", firstReceived);
        return "success";
    }




}

