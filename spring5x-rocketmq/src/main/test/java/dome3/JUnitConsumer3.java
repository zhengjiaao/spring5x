package dome3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Date: 2019-12-06 15:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：通过JUnit先运行消费者,再运行生产者,在消费者的控制台中能看到生产者发送的消息已经打印出来
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath*:META-INF/spring/rocketmq/RocketMQ-Consumer.xml"})
public class JUnitConsumer3 {

    /**
     * 通过JUnit先运行消费者(dome1.JUnitConsumer),再运行生产者(dome1.JUnitProducer),在消费者的控制台中能看到生产者发送的消息已经打印出来
     * 当然，先运行生产者，再运行消费者，消费者运行后也可以获取到消息。
     */
    @Test
    public void runConsumer() {
        System.out.println("Consumer Started.");

        // 下面的代码把线程阻塞住,这样就可以先运行消费者再运行生产者.当然不要也可以,不要的化就得先运行生产者,
        //再运行消费者,生产者先把消息发送到MQ上,消费者启动后从MQ上拿消息
        synchronized (JUnitConsumer3.class) {
            while (true) {
                try {
                    JUnitConsumer3.class.wait();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
