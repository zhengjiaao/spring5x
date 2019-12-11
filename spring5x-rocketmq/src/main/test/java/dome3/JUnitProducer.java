package dome3;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：消息发送
 */
public class JUnitProducer {

    @Test
    public void producerData2() throws Exception {

        //生产者组名
        String producerGroup="test_producer";

        //使用生产者组名称实例化
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //设置发送失败时的重试时间10s
        producer.setRetryTimesWhenSendFailed(10);
        //指定名称服务器地址
        producer.setNamesrvAddr("localhost:9876");
        //启动实例
        producer.start();
        //发送20条数据，模拟消息实体内容为5的这条消息在消费端消费失败(抛异常，MQ进行消息重发操作)
        //发送一条数据，消费端抛异常重试3次
        for (int i = 0; i < 1; i++) {
            //创建一个消息实例，指定主题topic、标记tag和消息主体body
            Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //调用发送消息将消息传递给代理之一。
            SendResult sendResult = producer.send(msg);
            System.out.println("sendResult: "+sendResult);
        }
        //一旦生产者实例不再使用，就立即关闭。
        producer.shutdown();
    }

}
