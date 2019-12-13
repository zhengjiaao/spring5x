package dome1;

import com.zja.rocketmq.producer.ProducerService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：发送消息
 */
@RunWith(JUnitPlatform.class)  // org.junit.platform.runner.JUnitPlatform
@ExtendWith(SpringExtension.class)  // org.springframework.test.context.junit.jupiter.SpringExtension
@ContextConfiguration({"classpath*:META-INF/spring/rocketmq/RocketMQ-Producer.xml"})
public class JUnitProducer {

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

}
