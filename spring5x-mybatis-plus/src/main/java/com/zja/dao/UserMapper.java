package com.zja.dao;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.zja.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ZhengJa
 * @description UserDao 接口
 * @data 2019/10/29
 */
public interface UserMapper extends Mapper<UserEntity> {

    //静态插入数据:通用方法
    int insertUser(UserEntity userEntity);

    //动态插入数据: mysql用法，id自增
    int insertUserMysql(UserEntity userEntity);
    //动态插入数据:oracle用法，id使用序列
    int insertUserOracle(UserEntity userEntity);

    //mybatis批量插入数据:mysql用法，id自增
    int mysqlBatchSaveUser(@Param("userEntityList") List<UserEntity> userEntities);
    //mybatis批量插入数据:oracle用法，id使用序列
    int oracleBatchSaveUser(@Param("userEntityList") List<UserEntity> userEntities);

    //按id查询用户
    UserEntity queryUserById(Long id);
    //查询所有用户
    List<UserEntity> queryAllUser();
    //按用户查询用户信息
    @Select("select * from userentity where username=#{userName}")
    List<UserEntity> queryByUserName(@Param("userName") String userName);

    //更新数据-改数据
    int updateUser(UserEntity userEntity);

    //删除数据
    int delUser(Integer id);

}
