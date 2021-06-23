package dome9;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * Date: 2019-12-06 16:28
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public class JunitBatchConsumer {

    /**
     * 消费者1-消息无序消费
     * 并发消费的消费速度要比有序消费更快
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String group_name="Concurrently_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        //设置为广播模式
        //广播模式(BROADCASTING),默认集群模式(CLUSTERING)
        //广播:每个消费者都消费订阅的主题消息。 集群：消费者平均消费主题消息，一个消息只能被一个消费者消费。
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setConsumerGroup(group_name);
        consumer.setNamesrvAddr("127.0.0.1:9876");

        //消息过滤：1.tag
        consumer.subscribe("TopicBatch","*");
        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("JunitConsumer Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                //属性原始id，方便去重
                String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                System.out.println("ThreadId: "+Thread.currentThread().getId()+" ,原始消息Id:"+orignMsgId+" ,queueId: "+msg.getQueueId() +" ,Keys: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
