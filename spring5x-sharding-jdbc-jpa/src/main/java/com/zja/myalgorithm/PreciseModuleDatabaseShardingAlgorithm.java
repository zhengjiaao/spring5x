package com.zja.myalgorithm;

import com.alibaba.fastjson.JSON;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * Date: 2019-12-16 15:32
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：自定义标准精确分库算法：使用精确分片算法（=与IN）
 */
public class PreciseModuleDatabaseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        System.out.println("collection:" + JSON.toJSONString(availableTargetNames) + ",preciseShardingValue:" + JSON.toJSONString(preciseShardingValue));
        for (String name : availableTargetNames) {
            // =与IN中分片键对应的值
            String value = String.valueOf(preciseShardingValue.getValue());
            // 分库的后缀
            int i = 1;
            // 求分库后缀名的递归算法
            if (name.endsWith("_" + countDatabaseNum(Long.parseLong(value), i))) {
                return name;
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * 计算该量级的数据在哪个数据库
     * @return
     */
    private String countDatabaseNum(long columnValue, int i){
        // ShardingSphereConstants每个库中定义的数据量
        long left = ShardingSphereConstants.databaseAmount * (i-1);
        long right = ShardingSphereConstants.databaseAmount * i;
        if(left < columnValue && columnValue <= right){
            return String.valueOf(i);
        }else{
            i++;
            return countDatabaseNum(columnValue, i);
        }
    }
}
