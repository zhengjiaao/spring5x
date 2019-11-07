package com.zja.rabbitmq.consumers;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * @author ZhengJa
 * @description 消费者应答模式：(发送确认,接收确认)
 * @data 2019/11/4
 */
public class ReceiveConfirmTestListener implements ChannelAwareMessageListener {
    /**
     *
     * @param message
     * @param channel
     * @return void
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        try{
            //System.out.println("consumer--:"+message.getMessageProperties()+":"+new String(message.getBody()));
            throw new RuntimeException();
            //// 确认消息
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            //TODO 业务处理
            System.out.println("进入异常模块");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
            //e.printStackTrace();
        }

    }
}
