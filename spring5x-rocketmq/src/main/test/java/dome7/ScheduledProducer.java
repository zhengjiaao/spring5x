package dome7;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * Date: 2019-12-09 16:27
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：发送预定的消息
 */
public class ScheduledProducer {

    /**
     * 什么是预定消息？
     * 预定的消息与正常的消息的不同之处在于，它们要等到指定的时间后才能传递。
     */
    public static void main(String[] args) throws Exception {
        //生产者组名
        String producerGroup= "scheduled_producer";
        //实例化一个生产者来发送预定的消息
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr("localhost:9876");
        //启动生产
        producer.start();
        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("TopicScheduled", ("Scheduled message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 此消息将在10秒后传递给使用者。
            message.setDelayTimeLevel(3);
            //发送消息
            SendResult sendResult = producer.send(message);
            System.out.println(sendResult);
        }

        // 使用后关闭生产者。
        producer.shutdown();
    }

    //验证：您应该看到消息被消耗比存储时间晚10秒。
}
