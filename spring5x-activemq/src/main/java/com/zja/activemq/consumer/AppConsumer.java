package com.zja.activemq.consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 消费者
 * 队列和主题的消费者
 * 订阅者
 * @program: jms-spring
 * @Date: 2018/12/3 10:57
 * @Author: Mr.Zheng
 * @Description:
 */
class AppConsumer {
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("jms/consumer.xml");
    }
}
