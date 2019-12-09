package com.zja.rocketmq.producer;

/**
 * Date: 2019-12-06 15:21
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface ProducerService {

    Object sendData() throws InterruptedException;
}
