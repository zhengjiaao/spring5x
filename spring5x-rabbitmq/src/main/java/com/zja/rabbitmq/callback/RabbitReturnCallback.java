package com.zja.rabbitmq.callback;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.UnsupportedEncodingException;

/**
 * @author ZhengJa
 * @description 失败后return回调 :消息发送失败返回监听器
 * @data 2019/11/4
 */
public class RabbitReturnCallback implements RabbitTemplate.ReturnCallback{

    /**使用场景：
     * exchange到queue成功,则不回调return
     * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange 交换器
     * @param routingKey 路由键
     * @return void
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println(" 进入 RabbitReturnCallback 回调 --> exchange到queue失败");
        //System.out.println("消息主体 message : "+message);
        byte[] body = message.getBody();
        try {
            System.out.println("返回消息内容 : "+new String(body,"utf-8"));

            System.out.println("消息回复代码 : "+replyCode);
            System.out.println("描述 : "+replyText);
            System.out.println("消息使用的交换器 exchange : "+exchange);
            System.out.println("消息使用的路由键 routing : "+routingKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
