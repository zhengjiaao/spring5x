package com.zja.activemq.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 主题消费者：必须实现监听器,监听队列的TextMessage 消息
 * @Author: Mr.Zheng
 * @Description: 消息监听器：监听到 消息目的 有消息后自动调用onMessage(Message message)方法
 */
public class TopicTextListener implements MessageListener {
    //添加了监听器,只要生产者发布了消息,监听器监听到有消息消费者就会自动消费(获取消息)
    @Override
    public void onMessage(Message message) {
        System.out.println("进入 TopicTextListener 监听器");

        //(第1种方式)没加转换器之前接收到的是文本消息
        TextMessage tm = (TextMessage) message;
        try {
            System.out.println("TopicTextListener监听到消息：\t" + tm.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
