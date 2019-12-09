package dome2;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-12-06 16:29
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 理想情况下消息顺序消费的结果应该是，同一个orderId下的消息的编号i值应该顺序递增，但是不同orderId之间的消费可以并行，即局部有序即可
 */
public class Consumer2 {

    /**
     * 顺序消费的consumer
     * @param args
     */
    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_3");
        consumer.setNamesrvAddr("127.0.0.1:9876");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("TopicTestjjj", "TagA");

        //消费者并行消费
        consumer.setConsumeThreadMin(3);
        consumer.setConsumeThreadMin(6);

        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
//                context.setAutoCommit(false);
                for (MessageExt msg : msgs) {
                    System.out.println("queueId:"+msg.getQueueId()+",orderId:"+new String(msg.getBody())+",i:"+msg.getKeys());
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("Consumer Started.%n");
    }
}
