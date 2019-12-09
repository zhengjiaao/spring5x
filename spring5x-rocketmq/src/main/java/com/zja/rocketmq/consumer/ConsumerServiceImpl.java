package com.zja.rocketmq.consumer;

import org.springframework.stereotype.Service;

/**
 * Date: 2019-12-06 14:57
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消费者：接收消息
 */
@Service
public class ConsumerServiceImpl implements ConsumerService{

    /**
     * 先运行消费者(ConsumerImpl),再运行生产者(ProducerImpl),在消费者的控制台中能看到生产者发送的消息已经打印出来
     */
    @Override
    public Object runConsumer() {
        System.out.println("Consumer Started.");

        // 下面的代码把线程阻塞住,这样就可以先运行消费者再运行生产者.当然不要也可以,不要的化就得先运行生产者,
        //再运行消费者,生产者先把消息发送到MQ上,消费者启动后从MQ上拿消息
        synchronized (ConsumerServiceImpl.class) {
            while (true) {
                try {
                    ConsumerServiceImpl.class.wait();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
