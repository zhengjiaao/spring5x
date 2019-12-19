package com.zja.dao;

import com.zja.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-12-18 16:34
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface AddressJpaRepositories extends JpaRepository<Address,Long> {
}
