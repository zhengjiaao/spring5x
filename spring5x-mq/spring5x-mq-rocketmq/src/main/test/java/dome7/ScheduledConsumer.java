package dome7;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Date: 2019-12-09 16:27
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：启动使用者以等待传入的订阅消息
 */
public class ScheduledConsumer {

    /**
     * 什么是预定消息？
     * 预定的消息与正常的消息的不同之处在于，它们要等到指定的时间后才能传递。
     */
    public static void main(String[] args) throws Exception {
        //消费者组名
        String consumerGroup= "scheduled_consumer";
        // Instantiate message consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");
        // Subscribe topics
        consumer.subscribe("TopicScheduled", "*");
        // Register message listener
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    //属性原始id，方便去重
                    //String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                    //System.out.println("ThreadId: "+Thread.currentThread().getId()+" ,MsgId: "+msg.getMsgId()+" ,原始消息Id:"+orignMsgId+" ,queueId: "+msg.getQueueId() +" ,Keys: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
                    //打印大约的延迟时间
                    System.out.println("MsgId： "+msg.getMsgId() +" ,大约延迟时间: "+ (System.currentTimeMillis() - msg.getStoreTimestamp()) + "ms later");
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // Launch consumer
        consumer.start();
        System.out.printf("ScheduledMessageConsumer Started.%n");
    }
}
