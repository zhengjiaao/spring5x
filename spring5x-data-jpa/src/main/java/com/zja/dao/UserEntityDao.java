package com.zja.dao;


import com.zja.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Date: 2019-12-11 17:18
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
//@Repository
public interface UserEntityDao extends JpaRepository<UserEntity,Long>{

    //查询所有对象，排序
    //@Override
    //List<UserEntity> findAll(Sort sort);


    //批量删除
    /*@Override
    void deleteInBatch(Iterable<UserEntity> entities);*/

    //删除所有
    /*@Override
    void deleteAllInBatch();*/

    /**
     * 使用@Query 创建查询
     * 描述：推荐使用这种方法，可以不用管参数的位置
     * 注意：UserEntity是实体类名称，不是表名称，userName是属性名称，不是表字段
     * @param userName 参数：用户名-不重复，具有唯一性
     */
    @Query("select u from UserEntity u where u.userName = :userName")
    UserEntity findUserByUserName(@Param("userName") String userName);

    /**
     * 占位符? 注意参数位置
     * 1表示第一个参数
     * @param age 参数：年龄
     */
    @Query("select u from UserEntity u where u.age = ?1")
    List<UserEntity> findUserByAge(Integer age);

    /**
     * 修改查询
     * 使用 @Query 来执行一个更新操作，用 @Modifying 来将该操作标识为修改查询，最终会生成一个更新的操作，而非查询操作
     * @param userName 用户名
     * @param newUserName 新的用户名
     */
    @Transactional
    @Modifying
    @Query(value="update UserEntity u set u.userName=:newUserName where u.userName like %:userName")
    int findByUpdateUserName(@Param("userName") String userName,@Param("newUserName") String newUserName);

    /**
     * 使用@Query来指定sql原始语句查询，只要设置nativeQuery为true
     * 注意：userentity为表明，username表字段
     * @param userName
     */
    @Query(value = "select * from userentity u where u.username like %?1",nativeQuery = true)
    List<UserEntity> findUserByLikeUserName(String userName);

}
