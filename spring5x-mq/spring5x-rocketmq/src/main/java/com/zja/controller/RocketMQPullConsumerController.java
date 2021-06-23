package com.zja.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 2019-12-10 10:56
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/rocketmq")
@Api(tags = {"RocketMQPullConsumerController"},description = "RocketMQ消费者pull接收消息")
public class RocketMQPullConsumerController {

    //保存上一次消费的消息位置
    private static final Map<MessageQueue,Long> OFFSE_TABLE = new HashMap<MessageQueue,Long>();

    @Autowired
    private DefaultMQPullConsumer pullConsumer;

    @ApiOperation(value = "手动下拉消息",notes = "pull接收消息", httpMethod = "GET")
    @GetMapping("pull/consumer/test1")
    public Object pullConsumer() throws Exception {
        // 从指定topic中拉取所有消息队列
        Set<MessageQueue> mqs = pullConsumer.fetchSubscribeMessageQueues("TopicPullTest");
        //遍历消息队列
        for(MessageQueue mq:mqs){
            try {
                // 获取消息的offset，指定从store中获取，设置上次消费消息下标
                long offset = pullConsumer.fetchConsumeOffset(mq,true);
                System.out.println("consumer from the queue:"+mq+":"+offset);
                while(true){
                    PullResult pullResult = pullConsumer.pullBlockIfNotFound(mq, null,
                            getMessageQueueOffset(mq), 32);
                    putMessageQueueOffset(mq,pullResult.getNextBeginOffset());
                    //根据结果状态，如果找到消息，批量消费消息
                    switch(pullResult.getPullStatus()){
                        case FOUND:
                            List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                            for (MessageExt m : messageExtList) {
                                System.out.println("Body: "+new String(m.getBody()));
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break;
                        case OFFSET_ILLEGAL:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "接收消息成功";
    }

    //保存上次消费的消息下标，这里使用了一个全局HashMap来保存
    private static void putMessageQueueOffset(MessageQueue mq,
                                              long nextBeginOffset) {
        OFFSE_TABLE.put(mq, nextBeginOffset);
    }

    // 获取上次消费的消息的下标
    private static Long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSE_TABLE.get(mq);
        if(offset != null){
            return offset;
        }
        return 0L;
    }
}
