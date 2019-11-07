package com.zja.activemq.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 队列消费者：必须实现监听器,监听队列的TextMessage 消息
 * @Author: Mr.Zheng
 * @Description: 消息监听器：监听到 消息目的 有消息后自动调用onMessage(Message message)方法
 */
public class QueueTextListener implements MessageListener {

    /**
     * 接收 TextMessage消息信息，没加转换器之前接收到的是文本消息
     * @param message 文本消息（一般直接传输json字符串，所以可以认为文本消息是最通用的）
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("进入 QueueTextListener 监听器");

        TextMessage textMessage = (TextMessage) message;

        try {  //打印出消息
            System.out.println("QueueTextListener监听到消息："+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
