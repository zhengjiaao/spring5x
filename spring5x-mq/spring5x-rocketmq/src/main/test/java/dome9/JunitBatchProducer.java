package dome9;

import com.zja.util.ListSplitter;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019-12-09 16:34
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：批处理发送消息：一批邮件的总大小不得超过1MiB。
 */
public class JunitBatchProducer {

    /*为什么要批处理？
        批量发送消息可提高传递小消息的性能。
        使用限制:
        同一批次的消息应具有：相同的主题，相同的waitStoreMsgOK，并且不支持计划。
        此外，一批邮件的总大小不得超过1MiB。
     */


    /**
     * 如何使用批处理
     * 如果您一次只发送不超过1MiB的消息，则可以轻松使用批处理：
     */
    @Test
    public void test1() throws Exception {

        //生产者组名
        String producerGroup = "Concurrently_producer";

        //使用生产者组名称实例化
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //设置发送失败时的重试时间10s
        producer.setRetryTimesWhenSendFailed(10);
        //指定名称服务器地址
        producer.setNamesrvAddr("localhost:9876");
        //启动实例
        producer.start();
        //主题
        String topic = "TopicBatch";
        //消息列表
        List<Message> messages = new ArrayList<>();
        //测试消息体大小超过最大值，max: 4194304
        for (int g = 0; g < 500000; g++) {
            messages.add(new Message(topic, "TagA", "OrderID" + g, ("Hello RocketMQ " + g).getBytes(RemotingHelper.DEFAULT_CHARSET)));
        }
        //测试发送一条消息，超过最大值，max: 4194304，报异常
        SendResult sendResult = producer.send(messages);
        System.out.println("SendStatus: "+sendResult.getSendStatus());

        //一旦生产者实例不再使用，就立即关闭。
        producer.shutdown();
    }

    /**
     * 分成列表
     * 仅当您发送大批量时，复杂性才会增加，并且您可能不确定它是否超过大小限制（1MiB）。
     * 目前，您最好拆分列表：
     */
    @Test
    public void test2() throws Exception {
        //生产者组名
        String producerGroup = "Concurrently_producer";

        //使用生产者组名称实例化
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //设置发送失败时的重试时间10s
        producer.setRetryTimesWhenSendFailed(10);
        //指定名称服务器地址
        producer.setNamesrvAddr("localhost:9876");
        //启动实例
        producer.start();

        //主题
        String topic = "TopicBatch";
        //消息列表
        List<Message> messages = new ArrayList<>();
        //测试消息体大小超过最大值，max: 4194304
        for (int g = 0; g < 500000; g++) {
            messages.add(new Message(topic, "TagA", "OrderID" + g, ("Hello RocketMQ " + g).getBytes(RemotingHelper.DEFAULT_CHARSET)));
        }

        //把大的列表分成小的列表
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message> listItem = splitter.next();
                //测试发送一条消息，超过最大值，max: 4194304，报异常
                SendResult sendResult = producer.send(listItem);
                System.out.println("SendStatus: "+sendResult.getSendStatus());
            } catch (Exception e) {
                e.printStackTrace();
                //handle the error
            }
        }
    }
}
