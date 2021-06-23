package dome8;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**-未成功
 * Date: 2019-12-06 16:28
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消息过滤：1.tag 2.sql（conf/broker.conf配置属性 enablePropertyFilter = true） 3.实现过滤器
 */
public class JunitFilterConsumer {

    /**
     * 消费者1-消息无序消费
     * 并发消费的消费速度要比有序消费更快
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String group_name="Concurrently_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setConsumerGroup(group_name);
        consumer.setNamesrvAddr("127.0.0.1:9876");

        //消息过滤：1.tag 2.sql 3.实现过滤器
        //consumer.subscribe("TopicConcurrently","TagA");
        consumer.subscribe("TopicConcurrently", MessageSelector.bySql("a between 0 and 3"));

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
