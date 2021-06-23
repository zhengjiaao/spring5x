package dome2;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Date: 2019-12-06 16:28
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 同一个orderId下，编号为10的消息先于编号为9的消息被消费，不是正确的顺序消费，即普通的并行消息消费，无法保证消息消费的顺序性
 */
public class Consumer1 {

    /**
     * 消费者1-消息无序消费
     * 并发消费的消费速度要比有序消费更快
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String group_name="Concurrently_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);

        consumer.setNamesrvAddr("127.0.0.1:9876");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("TopicOrderly", "*");
        //单个消费者中多线程并行消费
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMin(64);

        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("Consumer1 Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                System.out.println("ThreadName: "+Thread.currentThread().getName()+" ,queueId: "+msg.getQueueId() +" ,i: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
