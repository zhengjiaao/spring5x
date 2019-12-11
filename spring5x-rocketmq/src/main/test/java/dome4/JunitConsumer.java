package dome4;

import dome3.JUnitConsumer;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Date: 2019-12-09 16:06
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public class JunitConsumer {

    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        //消费者组名
        String consumerGroup = "test_consumer_group";

        //使用指定的使用者组名称实例化
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        //设置消耗线程最小值/最大值
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(64);
        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");
        //消费者订阅主题
        consumer.subscribe("TopicTest", "*");
        //消费者监听器
        consumer.registerMessageListener(new Listener());
        //启动消费者实例
        consumer.start();
        System.out.println("JUnitConsumer Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                System.out.println("ThreadId: "+Thread.currentThread().getId()+" ,queueId: "+msg.getQueueId() +" ,Keys: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
