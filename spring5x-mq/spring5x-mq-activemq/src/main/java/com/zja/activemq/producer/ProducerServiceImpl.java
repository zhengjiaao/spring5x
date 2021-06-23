package com.zja.activemq.producer;

import com.zja.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * 生产者：队列和主题
 * @Author: Mr.Zheng
 * @Description: 生产者：发送消息被消费者监听器监听到并接收消息
 */
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsQueueTemplate;

    @Autowired
    @Qualifier("jmsTopicTemplate")
    private JmsTemplate jmsTopicTemplate;

    //queueDestination 队列
    @Resource(name="queueTextDestination")
    Destination destinationQueueText;

    //queueDestination 队列
    @Resource(name="queueObjectDestination")
    Destination destinationQueueObject;

    //topicDestination 主题
    @Resource(name="topicTextDestination")
    Destination destinationTopicText;

    //topicDestination 主题
    @Resource(name="topicObjectDestination")
    Destination destinationTopicObject;

    /**
     * Queue 发送Text类型消息
     * @param message Text消息
     * @return void
     */
    @Override
    public void sendQueueTextMessage(final String message) {
        //使用JmsTemplate发送消息
        jmsQueueTemplate.send(destinationQueueText, new MessageCreator() {
            //创键一个消息，并没有发送
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage =session.createTextMessage(message);
                return textMessage;
            }
        });
        System.out.println("Queue发送的TextMessage消息内容："+message);
    }

    /**
     * Queue 队列发送对象类型消息，需要配置对象转换器
     * @param userEntity 对象消息
     * @return void
     */
    @Override
    public void sendQueueObjectMessage(UserEntity userEntity){
        //使用JmsTemplate发送消息
        jmsQueueTemplate.send(destinationQueueObject, new MessageCreator() {
            //创键一个消息，并没有发送
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage(userEntity);
                return objectMessage;
            }
        });
        System.out.println("Queue发送的ObjectMessage消息内容："+userEntity);
    }


    /**
     * Topic 主题发送Text类型消息
     * @param message Text消息
     * @return void
     */
    @Override
    public void sendTopicTextMessage(final String message) {
        //使用JmsTemplate发送消息
        jmsTopicTemplate.send(destinationTopicText, new MessageCreator() {
            //创键一个消息，并没有发送
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage =session.createTextMessage(message);
                return textMessage;
            }
        });
        System.out.println("Topic发送的TextMessage消息内容："+message);
    }

    /**
     * Topic 主题发送对象类型消息，需要配置对象转换器
     * @param userEntity 对象消息
     * @return void
     */
    @Override
    public void sendTopicObjectMessage(UserEntity userEntity){
        //使用JmsTemplate发送消息
        jmsTopicTemplate.send(destinationTopicObject, new MessageCreator() {
            //创键一个消息，并没有发送
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage(userEntity);
                return objectMessage;
            }
        });
        System.out.println("Topic发送的ObjectMessage消息内容："+userEntity);
    }

}
