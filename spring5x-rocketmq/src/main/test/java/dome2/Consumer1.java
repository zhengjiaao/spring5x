package dome2;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-12-06 16:28
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 同一个orderId下，编号为10的消息先于编号为9的消息被消费，不是正确的顺序消费，即普通的并行消息消费，无法保证消息消费的顺序性
 */
public class Consumer1 {

    /**
     * 正常集群消费的consumer: 消费无序
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_4");

        consumer.setNamesrvAddr("127.0.0.1:9876");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("TopicTestjjj", "*");
        //单个消费者中多线程并行消费
        consumer.setConsumeThreadMin(3);
        consumer.setConsumeThreadMin(6);

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
//                    System.out.println("收到消息," + new String(msg.getBody()));
                    System.out.println("queueId:"+msg.getQueueId()+",orderId:"+new String(msg.getBody())+",i:"+msg.getKeys());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
