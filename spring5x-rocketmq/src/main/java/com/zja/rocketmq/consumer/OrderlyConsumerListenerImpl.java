package com.zja.rocketmq.consumer;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Date: 2019-12-09 17:20
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：消息监听器有序
 */
public class OrderlyConsumerListenerImpl implements MessageListenerOrderly {

    AtomicLong consumeTimes = new AtomicLong(0);

    @SneakyThrows
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {

        /**设置自动提交
         * 1.当SUSPEND_CURRENT_QUEUE_A_MOMENT时，autoCommit设置为true或者false没有区别，本质跟消费相反，把消息从msgTreeMapTemp转移回msgTreeMap，等待下次消费
         * 2.当SUCCESS时，autoCommit设置为true时比设置为false多做了2个动作
         *      commit():本质是删除msgTreeMapTemp里的消息，msgTreeMapTemp里的消息在上面消费时从msgTreeMap转移过来的
         *      updateOffset():本质是把拉消息的偏移量更新到本地，然后定时更新到broker。
         */
        context.setAutoCommit(true);

        //获取消息
        for (MessageExt msg : msgs) {
            System.out.println("ThreadId: "+Thread.currentThread().getId()+" ,queueId: "+msg.getQueueId() +" ,i: "+msg.getKeys()+" ,Tags:"+msg.getTags() +" ,content: "+new String(msg.getBody(),"utf-8"));
        }

        this.consumeTimes.incrementAndGet();
        if ((this.consumeTimes.get() % 2) == 0) {
            return ConsumeOrderlyStatus.SUCCESS;
        }else if ((this.consumeTimes.get() % 5) == 0) {
            context.setSuspendCurrentQueueTimeMillis(3000);
            //挂起一会再消费
            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
