package dome8;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：消息发送-未成功
 */
public class JUnitFilterProducer {

    @Test
    public void producerData2() throws Exception {

        //生产者组名
        String producerGroup="Concurrently_producer";

        //使用生产者组名称实例化
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //设置发送失败时的重试时间10s
        producer.setRetryTimesWhenSendFailed(10);
        //指定名称服务器地址
        producer.setNamesrvAddr("localhost:9876");
        //启动实例
        producer.start();
        //发送多条消息
        for (int i = 0; i < 5; i++) {
            //创建一个消息实例，指定主题topic、标记tag和消息主体body
            //Message msg = new Message("TopicConcurrently", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            Message msg = new Message("TopicConcurrently",  ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            ////设置过滤参数
            msg.putUserProperty("a", String.valueOf(i));
            //调用发送消息将消息传递给代理之一。
            //这里调用的是同步的方式，所以会有返回结果
            SendResult sendResult = producer.send(msg);
            System.out.println(i+" "+sendResult.getSendStatus());
        }
        //一旦生产者实例不再使用，就立即关闭。
        producer.shutdown();
    }

}
