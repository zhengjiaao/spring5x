package com.zja.myalgorithm;

import com.alibaba.fastjson.JSON;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * Date: 2019-12-17 16:58
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：自定义分片算法(可分库、分表)：PreciseShardingAlgorithm
 */
public class MyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

    /**表分片策略：
     *   分片键：order_id
     *   分片算法：以order_id为分片键，分片策略为order_id % 2 + 1
     *      order_id为偶数，插入t_order_1
     *      order_id为奇数，插入t_order_2
     * @param availableTargetNames  可用目标名称
     * @param preciseShardingValue  精确的分片值
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        System.out.println("collection:" + JSON.toJSONString(availableTargetNames) + ",preciseShardingValue:" + JSON.toJSONString(preciseShardingValue));
        //availableTargetNames: t_order_1,t_order_2
        //shardingValue: {"logicTableName":"t_order","columnName":"order_id","value":396416249350848512}
        //collection:["t_order_1","t_order_2"],preciseShardingValue:{"logicTableName":"t_order","columnName":"order_id","value":396416249350848512}
        //name为两张订单表 t_order_1 和 t_order_2
        for (String name : availableTargetNames) {
            //订单号取模加1 与 订单表t_order_1 和 t_order_2的尾号做比对，如相等，就直接返回t_order_1 或 t_order_2
            if (name.endsWith(String.valueOf(preciseShardingValue.getValue() % 2 + 1))) {
                System.out.println("return name: " + name);
                return name;
            }
        }
        return null;
    }
}
