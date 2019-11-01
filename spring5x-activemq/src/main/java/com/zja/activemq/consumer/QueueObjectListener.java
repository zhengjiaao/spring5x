package com.zja.activemq.consumer;

import com.zja.entity.UserEntity;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 队列消费者：必须实现监听器,监听队列的ObjectMessage 消息
 * @Author: Mr.Zheng
 * @Description: 消息监听器：监听到 消息目的 有消息后自动调用onMessage(Message message)方法
 */
public class QueueObjectListener implements MessageListener {

    //接收 ObjectMessage消息信息，加了转换器之后接收到的ObjectMessage对象消息
    @Override
    public void onMessage(Message message) {
        System.out.println("进入 QueueObjectListener 监听器");

        ObjectMessage objectMessage = (ObjectMessage) message;
        UserEntity users;
        try { //打印出消息
            users = (UserEntity) objectMessage.getObject();
            System.out.println("QueueObjectListener监听到消息：\t" + users);
            //do something ...
        } catch (JMSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}
