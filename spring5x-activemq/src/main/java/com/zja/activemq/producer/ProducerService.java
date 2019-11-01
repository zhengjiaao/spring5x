package com.zja.activemq.producer;

import com.zja.entity.UserEntity;

/**
 * @program: jms-spring
 * @Date: 2018/11/30 17:31
 * @Author: Mr.Zheng
 * @Description:
 */
public interface ProducerService {

    //Queue 队列发送Text消息
    void sendQueueTextMessage(String message);
    //Queue 队列发送对象消息，需要配置对象转换器
    void sendQueueObjectMessage(UserEntity userEntity);

    //Topic 主题发送Text消息
    void sendTopicTextMessage(String message);
    //Topic 主题发送对象消息，需要配置对象转换器
    void sendTopicObjectMessage(UserEntity userEntity);
}
