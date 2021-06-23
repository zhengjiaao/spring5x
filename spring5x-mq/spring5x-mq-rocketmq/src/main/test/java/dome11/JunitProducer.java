package dome11;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * Date: 2019-12-09 16:24
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：发送消息
 */
public class JunitProducer {

    public static void main(String[] args) throws Exception {
        //生产者组名
        String producerGroup= "pull_producer";

        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 10; i++){
            Message msg = new Message("TopicPullTest",
                    "TagA",
                    "OrderID"+i,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            System.out.println("SendStatus: "+sendResult.getSendStatus());
        }
        producer.shutdown();
    }
}
