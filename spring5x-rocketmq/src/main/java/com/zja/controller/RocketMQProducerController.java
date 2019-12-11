package com.zja.controller;

import com.zja.rocketmq.producer.ProducerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
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
@Api(tags = {"RocketMQProducerController"},description = "RocketMQ发送消息简单实例")
public class RocketMQProducerController {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private DefaultMQProducer producer;

    @ApiOperation(value = "同步发送消息",notes = "在重要的通知消息，SMS通知，SMS营销系统等广泛的场景中使用可靠的同步传输", httpMethod = "GET")
    @GetMapping("producer/synchronous")
    public Object syn() throws Exception {

        for (int i = 0; i < 1; i++) {  // 发10条消息
            try {
                Message msg = new Message("TopicTest", // topic
                        "TagA", // tag
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
                );

                // 调用producer的send()方法发送消息
                // 这里调用的是同步的方式，所以会有返回结果
                SendResult sendResult = producer.send(msg);

                // 打印返回结果，可以看到消息发送的状态以及一些相关信息
                System.out.println("发送端返回结果: "+sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        //Shut down once the producer instance is not longer in use.
        ///producer.shutdown();
        //生产者：发送消息
        //this.producerService.sendData();
        return "发送消息成功";
    }

    @ApiOperation(value = "异步发送消息",notes = "异步传输通常用于对时间敏感的业务场景中", httpMethod = "GET")
    @GetMapping("producer/asynchronous")
    public Object asyn() throws Exception {

        //设置发送失败的重试次数
        producer.setRetryTimesWhenSendAsyncFailed(0);

        for (int i = 0; i < 10; i++) {  // 发10条消息
            final int index = i;
            //Create a message instance, specifying topic, tag and message body.
            //创建一个消息实例，指定主题、标记和消息主体。
            Message msg = new Message("TopicTest",
                    "TagA",
                    "OrderID188",
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
        ///producer.shutdown();
        return "发送消息成功";
    }

    @ApiOperation(value = "单向模式发送消息",notes = "单向传输用于要求中等可靠性的情况，例如日志收集", httpMethod = "GET")
    @GetMapping("producer/oneway")
    public Object oneway() throws Exception {

        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            //调用发送消息将消息传递给代理之一
            producer.sendOneway(msg);
        }
        //Shut down once the producer instance is not longer in use.
        ///producer.shutdown();
        return "发送消息成功";
    }

}
