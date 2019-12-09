package com.zja.rocketmq.consumer;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Date: 2019-12-06 14:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消费者监听器
 */
public class ConsumerListenerImpl implements MessageListenerConcurrently {
    /**
     *
     * @param list 消息
     * @param consumeConcurrentlyContext 返回消费状态(CONSUME_SUCCESS 消费成功,RECONSUME_LATER 消费失败，需要稍后重新消费)
     */
    @SneakyThrows
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        //System.out.println("消费者监听器 - " + " 收到新消息: " + list + " 线程：" + Thread.currentThread().getName());

        if (list !=null) {
            for (MessageExt msg : list) {
                System.out.println(">>>> 监听到消息: " + new String(msg.getBody(), "UTF-8"));
            }
        }

        //返回消费状态(CONSUME_SUCCESS 消费成功,RECONSUME_LATER 消费失败，需要稍后重新消费)
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
