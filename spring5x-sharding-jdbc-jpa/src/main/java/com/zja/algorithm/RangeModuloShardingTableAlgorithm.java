package com.zja.algorithm;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RangeModuloShardingTableAlgorithm implements RangeShardingAlgorithm<Long> {
    
    @Override
    public Collection<String> doSharding(final Collection<String> tableNames, final RangeShardingValue<Long> shardingValue) {
        Set<String> result = new LinkedHashSet<>();
        if (Range.closed(200000000000000000L, 400000000000000000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("0")) {
                    result.add(each);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return result;
    }
}
