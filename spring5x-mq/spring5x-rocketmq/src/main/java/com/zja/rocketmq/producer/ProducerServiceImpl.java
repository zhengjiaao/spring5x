package com.zja.rocketmq.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Date: 2019-12-06 14:56
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：发送消息
 */
@Service
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    private DefaultMQProducer producer;

    @Override
    public Object sendData() throws InterruptedException {
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
                System.out.println("发送端返回结果: "+sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        return "发送消息成功";
    }
}
