package dome6;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * Date: 2019-12-09 16:24
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：广播-生产者示例
 */
public class BroadcastProducer {

    /**
     * 广播正在向主题的所有订户发送一条消息。如果希望所有订阅者都收到有关主题的消息，那么广播是一个不错的选择。
     */
    public static void main(String[] args) throws Exception {
        //生产者组名
        String producerGroup= "broadcast_producer";

        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 10; i++){
            Message msg = new Message("TopicBroadcast",
                    "TagA",
                    "OrderID"+i,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }
}
