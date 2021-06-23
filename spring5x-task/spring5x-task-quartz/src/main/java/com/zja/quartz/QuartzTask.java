package com.zja.quartz;

import java.util.Date;

/**
 * @author ZhengJa
 * @description quartz 定时任务类
 * @data 2019/10/31
 */
public class QuartzTask {

    //执行方法
    public void execute(){
        System.out.println("控制台打印 时间: "+new Date());
    }
}
