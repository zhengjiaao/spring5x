package com.zja.dao;

import com.zja.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Date: 2019-12-18 9:31
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface UserJpaRepositories extends JpaRepository<User,Long> {

}
