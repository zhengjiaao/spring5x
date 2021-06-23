package com.zja.controller;

import com.zja.util.ListSplitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019-12-09 17:15
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/senior")
@Api(tags = {"RocketMQProducerSeniorController"},description = "RocketMQ发送消息高级实例")
public class RocketMQProducerSeniorController {

    @Autowired
    private DefaultMQProducer producer;

    @ApiOperation(value = "订购示例-有序消息",notes = "RocketMQ使用FIFO顺序提供有序消息,全局和分区排序消息的发送/接收", httpMethod = "GET")
    @GetMapping("producer/order")
    public Object orderProducer() throws Exception {

        //使用监听器：PushOrderlyConsumerListenerImpl
        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            int orderId = i % 10;
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);
            System.out.println("sendResult: "+sendResult);
            //System.out.printf("%s%n", sendResult);
        }
        return "发送消息成功";
    }

    @ApiOperation(value = "广播消息-无序消息",notes = "广播正在向主题的所有订户发送一条消息。如果希望所有订阅者都收到有关主题的消息，那么广播是一个不错的选择", httpMethod = "GET")
    @GetMapping("producer/broadcast")
    public Object broadcastProducer() throws Exception {
        //使用监听器：PushConsumerListenerImpl
        for (int i = 0; i < 10; i++){
            Message msg = new Message("TopicTest",
                    "TagA",
                    "OrderID188",
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        return "发送消息成功";
    }

    @ApiOperation(value = "预定消息-无序消息",notes = "预定的消息与正常的消息的不同之处在于，它们要等到指定的时间后才能传递", httpMethod = "GET")
    @GetMapping("producer/scheduled")
    public Object scheduledMessageProducer() throws Exception {
        //使用监听器：PushScheduledConsumerListenerImpl
        for (int i = 0; i < 10; i++){
            Message message = new Message("TopicTest", ("Hello scheduled message " + i).getBytes());
            // This message will be delivered to consumer 10 seconds later.
            //此消息将在10秒后传递给使用者。
            message.setDelayTimeLevel(3);
            // Send the message
            SendResult sendResult = producer.send(message);
            //System.out.printf("%s%n", sendResult);
        }
        return "发送消息成功";
    }

    //使用限制:
    //同一批次的消息应具有：相同的主题，相同的waitStoreMsgOK，并且不支持计划。
    //此外，一批邮件的总大小不得超过1MiB。
    @ApiOperation(value = "批量消息-无序消息",notes = "批量发送消息可提高传递小消息的性能,如果您一次只发送不超过1MiB的消息，则可以轻松使用批处理", httpMethod = "GET")
    @GetMapping("producer/batch")
    public Object batchMessageProducer() throws Exception {
        //使用监听器：PushConsumerListenerImpl
        String topic = "TopicTest";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID002", "Hello world 1".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID003", "Hello world 2".getBytes()));
        try {
            SendResult sendResult = producer.send(messages);
        } catch (Exception e) {
            e.printStackTrace();
            //handle the error
        }
        return "发送消息成功";
    }

    @ApiOperation(value = "批量消息拆分-无序消息",notes = "分成列表：仅当您发送大批量时，复杂性才会增加，并且您可能不确定它是否超过大小限制（1MiB）", httpMethod = "GET")
    @GetMapping("producer/batch/subList")
    public Object batchSubListMessageProducer() throws Exception {
        //使用监听器：PushConsumerListenerImpl
        String topic = "TopicPullTest";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID002", "Hello world 1".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID003", "Hello world 2".getBytes()));
        //then you could split the large list into small ones:
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message>  listItem = splitter.next();
                SendResult sendResult = producer.send(listItem);
            } catch (Exception e) {
                e.printStackTrace();
                //handle the error
            }
        }
        return "发送消息成功";
    }

    @ApiOperation(value = "筛选消息-无序消息",notes = "当标记tag不能满足要求时，使用SQL表达式来过滤出消息", httpMethod = "GET")
    @GetMapping("producer/filter")
    public Object filterProducer() throws Exception {
        //使用监听器：PushConsumerListenerImpl
        for (int i = 0; i < 10; i++){
            Message msg = new Message("TopicTest",
                    "*",
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            // Set some properties.
            msg.putUserProperty("a", String.valueOf(i));
            SendResult sendResult = producer.send(msg);
        }
        return "发送消息成功";
    }


}
