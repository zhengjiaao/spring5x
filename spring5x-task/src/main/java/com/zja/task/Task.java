package com.zja.task;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

/**
 * @author ZhengJa
 * @description Task 定时任务类
 * @data 2019/10/31
 */
public class Task {

    public void methodA() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodA 方法执行", LocalDateTime.now()));
    }

    @Async
    public void methodB() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodB 方法执行", LocalDateTime.now()));
        Thread.sleep(10 * 1000);
    }

    public void methodC() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodC 方法执行", LocalDateTime.now()));
    }

}
