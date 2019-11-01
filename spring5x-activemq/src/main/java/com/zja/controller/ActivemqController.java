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
    //Queue 队列发送对象消息，需要配置对象转换器
    @GetMapping("sendQueueObjectMessage")
    public void sendQueueObjectMessage(){
        this.producerService.sendQueueObjectMessage(new UserEntity(1,"QueueObject",23));
    }

    //Topic 主题发送Text消息
    @GetMapping("sendTopicTextMessage")
    public void sendTopicTextMessage(){
        String message = "TopicText";
        this.producerService.sendTopicTextMessage(message);
    }
    //Topic 主题发送对象消息，需要配置对象转换器
    @GetMapping("sendTopicObjectMessage")
    public void sendTopicObjectMessage(){
        this.producerService.sendTopicObjectMessage(new UserEntity(2,"TopicObject",20));
    }
}
