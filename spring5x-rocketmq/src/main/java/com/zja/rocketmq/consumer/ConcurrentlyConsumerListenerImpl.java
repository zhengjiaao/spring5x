package com.zja.rocketmq.consumer;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2019-12-06 14:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：并发消息监听器
 */
public class ConcurrentlyConsumerListenerImpl implements MessageListenerConcurrently {
    /**
     * @param list                       消息
     * @param consumeConcurrentlyContext 返回消费状态(CONSUME_SUCCESS 消费成功,RECONSUME_LATER 消费失败，需要稍后重新消费)
     */
    @SneakyThrows
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        //System.out.println("消息条数： "+list.size());
        //通用接收消息
        for (MessageExt msg : list) {
            System.out.println("ThreadId: " + Thread.currentThread().getId() + " ,queueId: " + msg.getQueueId() + " ,i: " + msg.getKeys() + " ,Tags:" + msg.getTags() + " ,content: " + new String(msg.getBody(), "utf-8"));
        }

        //预定消息-测试 10秒后接收到
        /*for (MessageExt message : list) {
            // Print approximate delay time period
            System.out.println("Receive message[msgId=" + message.getMsgId() + "] "
                    + (System.currentTimeMillis() - message.getStoreTimestamp()) + "ms later");
        }*/

        //重试机制-测试,发送一条消息,抛异常重试3次
        /*MessageExt msg = list.get(0);
        try {
            String topic = msg.getTopic();
            String msgBody = new String(msg.getBody(), "utf-8");
            String tags = msg.getTags();
            System.out.println("收到消息: " + " topic:" + topic + " ,tags:" + tags + " ,msgBody:" + msgBody);
            //属性原始id，方便去重
            String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
            System.out.println("orignMsgId： " + orignMsgId);
            //TimeUnit.SECONDS.sleep(20);
            //int a=1/0;  //抛异常测试重试次数
        } catch (Exception e) {
            e.printStackTrace();
            //允许重发3次,重试3次后结束并记录到日志
            if (msg.getReconsumeTimes() == 3) {
                System.err.println("---------记录日志---------");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }*/

        //返回消费状态(CONSUME_SUCCESS 消费成功,RECONSUME_LATER 消费失败，需要稍后重新消费)
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
