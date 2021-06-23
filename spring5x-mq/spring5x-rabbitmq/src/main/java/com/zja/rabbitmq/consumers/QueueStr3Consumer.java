package com.zja.rabbitmq.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description 消费者
 * @data 2019/11/4
 */
public class QueueStr3Consumer implements MessageListener {

    /**
     * 消费者接收消息
     * @param message 推荐使用字节
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        MessageProperties m=message.getMessageProperties();
        //System.out.println("m "+m);
        String msg= null;
        try {
            //utf-8 解决 消费者接收中文消息乱码
            msg = new String (message.getBody(),"utf-8");
            System.out.println("QueueStr3Consumer消费掉了:  "+msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
