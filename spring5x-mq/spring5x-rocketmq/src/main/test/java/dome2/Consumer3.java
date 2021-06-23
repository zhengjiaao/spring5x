package dome2;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2019-12-06 16:29
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：输出结果： 理想情况下消息顺序消费的结果应该是，同一个orderId下的消息的编号i值应该顺序递增，但是不同orderId之间的消费可以并行，即局部有序即可
 */
public class Consumer3 {

    /**
     * 消费者2-消息有序消费
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Consumer3 consumer3 = new Consumer3();
    }

    public Consumer3() throws Exception{
        String group_name="orderly_consumer";

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);
        consumer.setNamesrvAddr("127.0.0.1:9876");

        /**
         * 设置消费者第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //订阅主题，以及过滤的标签内容
        consumer.subscribe("TopicOrderly", "*");
        //消费者并行消费的线程
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMin(64);
        //注册监听器
        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("Consumer3 Started.");
    }

    //MessageListenerOrderly 用一个线程去监听一个队列
    static class Listener implements MessageListenerOrderly{
        //随机
        private Random random = new Random();

        @SneakyThrows
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            //设置自动提交
            context.setAutoCommit(true);

            for (MessageExt msg : msgs) {
                System.out.println("ThreadName: "+Thread.currentThread().getName()+" ,queueId: "+msg.getQueueId() +" ,i: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
            }
            try {
                //模拟业务处理中......
                TimeUnit.SECONDS.sleep(random.nextInt(5));
            }catch (Exception e){
                e.printStackTrace();
                //return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}
