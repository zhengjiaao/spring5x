package com.zja.util;

import org.springframework.stereotype.Component;

/**
 * Desc：自定义生成递增趋势id，目的支持跨数据库
 */
@Component
public class MyId {

    public long generateId() {
        SnowFlake snowFlake = new SnowFlake(2, 3);

        System.out.println(snowFlake.nextId());
        return snowFlake.nextId();
    }
}
