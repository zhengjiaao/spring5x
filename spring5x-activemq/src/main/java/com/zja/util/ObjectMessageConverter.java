package com.zja.util;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;

/**
 * @author ZhengJa
 * @description 功能说明:通用的消息对象转换类
 * @data 2019/10/31
 */
public class ObjectMessageConverter implements MessageConverter {
    //把一个Java对象转换成对应的JMS Message (生产消息的时候)
    @Override
    public Message toMessage(Object object, Session session)
            throws JMSException, MessageConversionException {

        return session.createObjectMessage((Serializable) object);
    }

    //把一个JMS Message转换成对应的Java对象 (消费消息的时候)
    @Override
    public Object fromMessage(Message message) throws JMSException,
            MessageConversionException {
        ObjectMessage objMessage = (ObjectMessage) message;
        return objMessage.getObject();
    }
}
