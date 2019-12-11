package dome5;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Date: 2019-12-09 16:13
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：订阅消息样本代码
 */
public class OrderedConsumer {

    /**
     * 订购实例
     * RocketMQ使用FIFO顺序提供有序消息。
     * 下面的示例演示了全局和分区排序消息的发送/接收
     */
    public static void main(String[] args) throws Exception {
        String consumerGroup = "orderly_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //设置消耗线程最小值/最大值
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(64);
        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");
        //消费者订阅主题
        consumer.subscribe("TopicOrderly", "TagA || TagC || TagD");
        //消费者监听器
        consumer.registerMessageListener(new Listener());
        //启动消费者实例
        consumer.start();

        System.out.println("Consumer Started.");
    }

    static class Listener implements MessageListenerOrderly{

        AtomicLong consumeTimes = new AtomicLong(0);

        @SneakyThrows
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            context.setAutoCommit(true);
            for (MessageExt msg : msgs) {
                //属性原始id，方便去重
                String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                System.out.println("ThreadId: "+Thread.currentThread().getId()+" ,原始消息Id:"+orignMsgId+" ,queueId: "+msg.getQueueId() +" ,Keys: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            this.consumeTimes.incrementAndGet();
            if ((this.consumeTimes.get() % 2) == 0) {
                return ConsumeOrderlyStatus.SUCCESS;
            }else if ((this.consumeTimes.get() % 5) == 0) {
                context.setSuspendCurrentQueueTimeMillis(3000);
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}
