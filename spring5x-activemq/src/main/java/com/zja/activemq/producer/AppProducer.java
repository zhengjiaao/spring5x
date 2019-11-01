package com.zja.activemq.producer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 发布消息者：启动
 *
 * 队列模式为：消费者，启动一个消费者消费所有信息，第二个消费者接收不到。而多个消费者已经启动，他们平分消息
 * 主题模式为：订阅者，要先启动订阅者；所有订阅者都接收全部消息。
 *
 *
 * @program: jms-spring
 * @Date: 2018/12/3 9:31
 * @Author: Mr.Zheng
 * @Description:
 */
public class AppProducer {

    public static void main(String[] args) {
        //获取上下文内容
        ApplicationContext context = new ClassPathXmlApplicationContext("jms/producer.xml");
        //从上下文里获取配置的ProducerService
        ProducerService service = context.getBean(ProducerService.class);

        for(int i=0;i<100;i++){
            service.sendQueueTextMessage("test"+i);//发送100条消息
        }

    }
}
