package com.zja.controller;

import com.zja.rocketmq.consumer.ConsumerService;
import com.zja.rocketmq.producer.ProducerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date: 2019-12-06 15:12
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/rocketmq")
@Api(tags = {"RocketMQController"},description = "RocketMQ消息中间价")
public class RocketMQController {

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private DefaultMQProducer producer;

    @ApiOperation(value = "1、先启动消费者",notes = "先开启消费者，生产者再发送消息", httpMethod = "GET")
    @GetMapping("start/consumer")
    public Object executeConsumer(){
        return consumerService.runConsumer();
    }

    @ApiOperation(value = "2、生产者发送消息",notes = "先开启消费者，生产者再发送消息", httpMethod = "GET")
    @GetMapping("start/producer")
    public Object sendData() throws Exception {

        for (int i = 0; i < 10; i++) {  // 发10条消息
            try {
                Message msg = new Message("TopicTest", // topic
                        "TagA", // tag
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)// body
                );

                // 调用producer的send()方法发送消息
                // 这里调用的是同步的方式，所以会有返回结果
                SendResult sendResult = producer.send(msg);

                // 清理资源，关闭网络连接，注销自己
                producer.shutdown();
                // 打印返回结果，可以看到消息发送的状态以及一些相关信息
                System.out.println("发送端返回结果: "+sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        return "发送消息成功";

    }

}
