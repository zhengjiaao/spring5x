package dome11;

import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 2019-12-11 14:26
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：手动拉取指定队列上的消息
 */
public class JunitPullConsumer {
    //理论来说，应该保存到redis等缓存或数据库中，这样不至于挂掉数据丢失
    //保存上一次消费的消息位置,Map<key,value> key 队列，value 这个队拉取数据的最后位置
    private static final Map<MessageQueue,Long> OFFSE_TABLE = new HashMap<MessageQueue,Long>();

    public static void main(String[] args) throws Exception {
        DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer();
        //广播
        pullConsumer.setMessageModel(MessageModel.BROADCASTING);
        pullConsumer.setConsumerGroup("pull_consumer");
        pullConsumer.setNamesrvAddr("localhost:9876");
        pullConsumer.start();
        System.out.println("pullConsumer Started.");

        // 从指定topic中拉取所有消息队列(默认 4个队列)
        Set<MessageQueue> mqs = pullConsumer.fetchSubscribeMessageQueues("TopicPullTest");
        //遍历消息队列
        for(MessageQueue mq:mqs){
            try {
                // 获取消息的offset，指定从store中获取，设置上次消费消息下标
                long offset = pullConsumer.fetchConsumeOffset(mq,true);
                System.out.println("consumer from the queue:"+mq+":"+offset);
                while(true){
                    //从队列中获取数据，从什么位置开始拉取数据，单次拉取32条数据
                    PullResult pullResult = pullConsumer.pullBlockIfNotFound(mq, null,
                            getMessageQueueOffset(mq), 32);
                    //保存上次消费的消息下标，这里使用了一个全局HashMap来保存
                    putMessageQueueOffset(mq,pullResult.getNextBeginOffset());
                    //根据结果状态，如果找到消息，批量消费消息
                    switch(pullResult.getPullStatus()){
                        //发现有消息
                        case FOUND:
                            //消费消息
                            List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                            for (MessageExt m : messageExtList) {
                                System.out.println("QueueId:"+m.getQueueId()+" ,Body: "+new String(m.getBody()));
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            System.out.println("没有新的数据...");
                            break;
                        case OFFSET_ILLEGAL:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pullConsumer.shutdown();
        System.out.println("pullConsumer Shutdown.");
    }

    /**
     * 保存上次消费的消息下标，这里使用了一个全局HashMap来保存
     * @param mq 队列
     * @param nextBeginOffset 此队列最后拉取的位置
     */
    private static void putMessageQueueOffset(MessageQueue mq,
                                              long nextBeginOffset) {
        OFFSE_TABLE.put(mq, nextBeginOffset);
    }

    /**
     * 获取上次消费的消息的下标
     * @param mq 队列
     */
    private static Long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSE_TABLE.get(mq);
        if(offset != null){
            return offset;
        }
        return 0L;
    }

}
