package dome3;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
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
@ContextConfiguration({"classpath*:META-INF/spring/rocketmq/RocketMQ-Producer.xml"})
public class JUnitProducer3 {

    @Autowired
    private DefaultMQProducer producer;

    @Test
    public void producerData() throws InterruptedException {
        for (int i = 0; i < 10; i++) {  // 发10条消息
            try {
                Message msg = new Message("TopicTest", // topic
                        "TagA", // tag
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)// body
                );

                // 调用producer的send()方法发送消息
                // 这里调用的是同步的方式，所以会有返回结果
                SendResult sendResult = producer.send(msg);

                // 打印返回结果，可以看到消息发送的状态以及一些相关信息
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
    }


    @Test
    public void producerData2()  {
        try {
            /*MQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
            ((DefaultMQProducer) producer).setNamesrvAddr("111.231.110.149:9876");
            producer.start();*/

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
        } catch (InterruptedException e) {
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
