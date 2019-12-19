package com.zja.myalgorithm;

/**
 * Date: 2019-12-16 15:37
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：常量
 */
public class ShardingSphereConstants {
    /**
     * 订单、优惠券相关的表，按用户数量分库，64w用户数据为一个库
     * (0,64w]
     */
    public static int databaseAmount = 640000;

    /**
     * 一个订单表里存10000的用户订单
     * (0,1w]
     */
    public static int tableAmount = 10000;
}
