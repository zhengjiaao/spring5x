package dome3;

import dome2.Consumer1;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Date: 2019-12-06 15:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消费者：消息接收
 */
public class JUnitConsumer {

    /**
     * 测试消费异常 重发机制 3次
     */
    public static void main(String[] args) throws Exception {
        //消费者组名
        String consumerGroup = "test_consumer_group";

        //使用指定的使用者组名称实例化
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        //设置消耗线程最小值/最大值
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(64);
        //设置MQ服务地址
        consumer.setNamesrvAddr("localhost:9876");
        //消费者订阅主题
        consumer.subscribe("TopicTest", "*");
        //消费者监听器
        consumer.registerMessageListener(new Listener());
        //启动消费者实例
        consumer.start();
        System.out.println("JUnitConsumer Started.");
    }

    //MessageListenerConcurrently 用多个线程去监听一个队列
    static class Listener implements MessageListenerConcurrently{
        @SneakyThrows
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            //重试机制-测试,发送一条消息,抛异常重试3次
            MessageExt msg = msgs.get(0);
            try {
                String topic = msg.getTopic();
                String msgBody = new String(msg.getBody(), "utf-8");
                String tags = msg.getTags();
                //属性原始id，方便去重
                String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                System.out.println("orignMsgId：" + orignMsgId + " ,topic:" + topic + " ,tags:" + tags + " ,msgBody:" + msgBody);
                //TimeUnit.SECONDS.sleep(20);
                int a=1/0;  //抛异常测试重试次数
            } catch (Exception e) {
                e.printStackTrace();
                //允许重发3次,重试3次后结束并记录到日志
                if (msg.getReconsumeTimes() == 3) {
                    System.err.println("---------记录日志---------");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

}
