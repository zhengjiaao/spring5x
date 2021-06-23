package dome10;

import com.sun.webkit.LoadListenerClient;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Date: 2019-12-06 15:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：生产者：消息发送-未成功
 */
public class JUnitTransactionProducer {

    /**
     * 事务
     */
    @Test
    public void producerData() throws Exception {

        //生产者组名
        String producerGroup="transaction_producer";

        //使用生产者组名称实例化
        TransactionMQProducer producer = new TransactionMQProducer(producerGroup);

        //事务回查最小并发数
        producer.setCheckThreadPoolMinSize(5);
        //事务回查最大并发数
        producer.setCheckThreadPoolMaxSize(20);
        //队列数
        producer.setCheckRequestHoldMax(2000);
        //指定名称服务器地址
        producer.setNamesrvAddr("localhost:9876");
        //启动实例，初始化一次即可
        //注意：切记不可在每次发送消息时，都调用start方法
        producer.start();

        //注册事务检查监听器
        producer.setTransactionCheckListener(new Listener());

        LocalTransactionExecuterImpl transactionExecuter = new LocalTransactionExecuterImpl();

        for(int i=1;i<=1;i++){
            Message msg = new Message("TopicTransaction","TagA","KEY "+i,("Hello Rocket "+i).getBytes("utf-8"));
            SendResult sendResult = producer.send(msg, (MessageQueueSelector) transactionExecuter,"tq");
            System.out.println("SendStatus： "+sendResult.getSendStatus() +" ,"+i);
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        //一旦生产者实例不再使用，就立即关闭。
        //producer.shutdown();
    }

    /**
     * 事务检查监听器 -- 已经被阿里巴巴去掉了，mq通过tcp主调掉此方法不会回调成功
     */
    class Listener implements TransactionCheckListener{
        @SneakyThrows
        @Override
        public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
            System.out.println("state -- "+new String(msg.getBody(),"utf-8"));
            //if 数据入库真实发生变化，则再次提交状态
            //else 数据库没有发生变化，则直接忽略该数据回滚即可
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }

    /**
     * 执行本地事务，由客户端回调
     */
    class LocalTransactionExecuterImpl implements LocalTransactionExecuter{

        @Override
        public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
            System.out.println(msg.toString());
            System.out.println("msg ="+new String(msg.getBody()));
            System.out.println("arg ="+arg);
            String tag = msg.getTags();

            System.out.println("这里执行入库操作...入库操作...");
            /*if (tag.equals("Transaction3")){
                //这里有一个分阶段提交任务的概念
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }*/

            return LocalTransactionState.COMMIT_MESSAGE;

            //如果执行的业务操作成功，但是没有返回标识信息位置，则认为第二次没有发出
            //return LocalTransactionState.ROLLBACK_MESSAGE;

            //return LocalTransactionState.UNKNOW;
        }
    }


}
