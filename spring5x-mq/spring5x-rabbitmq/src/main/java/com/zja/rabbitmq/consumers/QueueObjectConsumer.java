package com.zja.rabbitmq.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * @author ZhengJa
 * @description 对象接收消息
 * @data 2019/11/4
 */
public class QueueObjectConsumer implements MessageListener {

    /**
     * 消费者接收消息
     * @param message 对象必须实现序列化
     * @return void
     */
    @Override
    public void onMessage(Message message) {
    }
}
