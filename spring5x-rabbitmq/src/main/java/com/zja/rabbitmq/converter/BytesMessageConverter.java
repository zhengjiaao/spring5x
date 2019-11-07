package com.zja.rabbitmq.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.lang.reflect.Type;

/**
 * @author ZhengJa
 * @description 发送方消息转换器
 * @data 2019/11/7
 */
public class BytesMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        return null;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties, Type genericType) throws MessageConversionException {
        return null;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return null;
    }
}
