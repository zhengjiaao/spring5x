package com.zja.rabbitmq.callback;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author ZhengJa
 * @description 确认后回调方
 * @data 2019/11/4
 */
public class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback {

    /**如果消息没有到exchange,则confirm回调,ack=false
     * 如果消息到达exchange,则confirm回调,ack=true
     * @param correlationData
     * @param ack true/false  true是成功发送到(路由交换机)broker / false 没有发送成功到交换机
     * @param cause
     * @return void
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("进入 RabbitConfirmCallback：ack消息到达(true)/没有(false)到达 exchange");
        System.out.println("消息唯一标识："+correlationData);
        System.out.println("确认结果 ack："+ack);
        System.out.println("失败原因："+cause);
    }
}
