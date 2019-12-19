package com.zja.dao;

import com.zja.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Date: 2019-12-17 15:01
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface OrderJpaRepositories extends JpaRepository<Order,Long> {

}
