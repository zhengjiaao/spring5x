package com.zja.activemq.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 消费者：必须实现监听器,监听生产者(queue队列和topic主题)发送的消息
 * @Author: Mr.Zheng
 * @Description: 消息监听器：监听到 消息目的 有消息后自动调用onMessage(Message message)方法
 */
public class ConsumerMessageListener implements MessageListener {
    //添加了监听器,只要生产者发布了消息,监听器监听到有消息消费者就会自动消费(获取消息)
    @Override
    public void onMessage(Message message) {
        System.out.println("进入 ConsumerMessageListener 公共监听器");
        //(第1种方式)没加转换器之前接收到的是文本消息
        TextMessage tm = (TextMessage) message;

        //(第2种方式)加了转换器之后接收到的ObjectMessage对象消息
        //ObjectMessage objMsg=(ObjectMessage) message;
        //UserEntity users;
        try {
            System.out.println("ConsumerMessageListener监听到文本消息：\t" + tm.getText());

            // users = (UserEntity) objMsg.getObject();
            // System.out.println("QueueMessageListener监听到了消息：\t" + users);

            // do something ...
        } catch (JMSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
