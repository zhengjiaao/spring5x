package com.zja.rocketmq.consumer;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.MessageQueueListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Date: 2019-12-06 14:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：pull消费者监听器,主动拉取消息
 */
public class PullConsumerListenerImpl implements MessageQueueListener {

    @Override
    public void messageQueueChanged(String s, Set<MessageQueue> set, Set<MessageQueue> set1) {
        System.out.println("*************Pull消费者监听器,主动拉取消息*************");
        System.out.println("主题 s: "+s);
        if (set !=null){
            Iterator<MessageQueue> iterator = set.iterator();
            while (iterator.hasNext()){
                MessageQueue messageQueue = iterator.next();
                System.out.println("messageQueue1: "+messageQueue);
            }
        }
        if (set1 !=null){
            Iterator<MessageQueue> queueIterator = set1.iterator();
            while (queueIterator.hasNext()){
                MessageQueue messageQueue2 = queueIterator.next();
                System.out.println("messageQueue2: "+messageQueue2);
            }
        }
    }
}
