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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消息发送者
 */
public class JUnitProducer {

    @Test
    public void producerData(){
        try {
            String gruop_name="orderly_producer";
            MQProducer producer = new DefaultMQProducer(gruop_name);
            ((DefaultMQProducer) producer).setNamesrvAddr("127.0.0.1:9876");
            producer.start();

            //顺序发送10条编号为0到9的，orderId为1 的消息
            new Thread(() -> {
                Integer orderId = 1;
                sendMessage(producer, orderId);
            }).start();
            //顺序发送10条编号为0到9的，orderId为2 的消息
            new Thread(() -> {
                Integer orderId = 2;
                sendMessage(producer, orderId);
            }).start();
            //顺序发送10条编号为0到9的，orderId为3 的消息
            new Thread(() -> {
                Integer orderId = 3;
                sendMessage(producer, orderId);
            }).start();
            //sleep 30秒让消息都发送成功再关闭
            Thread.sleep(1000*20);

            producer.shutdown();
        } catch (InterruptedException | MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     * @param producer 生产者
     * @param orderId 队列下标
     */
    private static void sendMessage(MQProducer producer, Integer orderId) {
        for (int i = 0; i < 5; i++) {
            try {
                //topic,tags,keys,body
                Message msg = new Message("TopicOrderly", "TagA", "KEY"+i,
                                ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据： 如果使用顺序消费，则必须自己实现MessageQueueSelector，保证消息进入同一个队列。
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId); //orderId 是队列下标
                System.out.println("message send,orderId: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            try {
                //topic,tags,keys,body
                Message msg = new Message("TopicOrderly", "TagB", "KEY"+i,
                        ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据： 如果使用顺序消费，则必须自己实现MessageQueueSelector，保证消息进入同一个队列。
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId); //orderId 是队列下标
                System.out.println("message send,orderId: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            try {
                //topic,tags,keys,body
                Message msg = new Message("TopicOrderly", "TagC", "KEY"+i,
                        ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据： 如果使用顺序消费，则必须自己实现MessageQueueSelector，保证消息进入同一个队列。
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId); //orderId 是队列下标
                System.out.println("message send,orderId: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
