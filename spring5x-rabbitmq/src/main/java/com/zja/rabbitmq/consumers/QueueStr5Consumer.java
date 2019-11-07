package com.zja.rabbitmq.consumers;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * @author ZhengJa
 * @description 消费者应答模式：(发送确认,接收确认)
 * @data 2019/11/4
 */
public class QueueStr5Consumer implements ChannelAwareMessageListener {

    /**
     * 消费者应答模式：(发送确认,接收确认)
     * @param message
     * @param channel
     * @return void
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg= null;
        try{
            //utf-8 解决 消费者接收中文消息乱码
            msg = new String (message.getBody(),"utf-8");
            System.out.println("QueueStr5Consumer消费掉了:  "+msg);
            //channel.basicAck 这个就是手动确认的函数:
            // 如果我们不在消费者中调用这个函数，那么rabbitmq就会一直的推送消息给消费者,也是就是说上面的输出语句会被重复的执行
            //如果执行了这个手动确认之后，rabbitmq就不会向这个消费者推送消息了
            //函数的第二个参数boolean标识的是如果为true标识所有的消费者都已经收到了这个消息，如果为false标识仅仅是当前的消费这个收到的这个消息
            //如果是是一个消费者，自然是true和false都是一样的
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            //e.printStackTrace();
            //TODO 业务处理
            System.out.println("QueueStr5Consumer 消费失败");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }
    }
}
