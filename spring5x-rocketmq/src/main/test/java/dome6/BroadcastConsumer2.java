package dome6;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * Date: 2019-12-09 16:23
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：广播-消费者示例
 */
public class BroadcastConsumer2 {

    //广播正在向主题的所有订户发送一条消息。如果希望所有订阅者都收到有关主题的消息，那么广播是一个不错的选择。
    public static void main(String[] args) throws Exception {
        //消费者组名
        String consumerGroup= "broadcast_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        //设置为广播模式
        //广播模式(BROADCASTING),默认集群模式(CLUSTERING)
        //广播:每个消费者都消费订阅的主题消息。 集群：消费者平均消费主题消息，一个消息只能被一个消费者消费。
        consumer.setMessageModel(MessageModel.BROADCASTING);

        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");

        consumer.subscribe("TopicBroadcast", "TagA");

        consumer.registerMessageListener(new Listener());

        consumer.start();
        System.out.printf("BroadcastConsumer2 Started.%n");
    }

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
