package dome2;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)  // org.junit.platform.runner.JUnitPlatform
@ExtendWith(SpringExtension.class)  // org.springframework.test.context.junit.jupiter.SpringExtension
//@ContextConfiguration({"classpath*:META-INF/spring/rocketmq/RocketMQ-Producer.xml"})
public class JUnitProducer {

    /*@Autowired
    private DefaultMQProducer producer;*/

    @Test
    public void producerData() throws InterruptedException {
        try {
            MQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
            ((DefaultMQProducer) producer).setNamesrvAddr("127.0.0.1:9876");
            producer.start();

            //顺序发送100条编号为0到99的，orderId为1 的消息
            new Thread(() -> {
                Integer orderId = 1;
                sendMessage(producer, orderId);
            }).start();
            //顺序发送100条编号为0到99的，orderId为2 的消息
            new Thread(() -> {
                Integer orderId = 2;
                sendMessage(producer, orderId);
            }).start();
            //sleep 30秒让消息都发送成功再关闭
            Thread.sleep(1000*30);

            producer.shutdown();
        } catch (InterruptedException | MQClientException e) {
            e.printStackTrace();
        }
    }

    //发送消息
    private static void sendMessage(MQProducer producer, Integer orderId) {
        for (int i = 0; i < 100; i++) {
            try {
                Message msg =
                        new Message("TopicTestjjj", "TagA", i + "",
                                (orderId + "").getBytes(RemotingHelper.DEFAULT_CHARSET));
                //RocketMQ通过MessageQueueSelector中实现的算法来确定消息发送到哪一个队列上
                // RocketMQ默认提供了两种MessageQueueSelector实现：随机/Hash
                //根据业务实现自己的MessageQueueSelector来决定消息按照何种策略发送到消息队列中
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId);
                System.out.println("message send,orderId:" + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
