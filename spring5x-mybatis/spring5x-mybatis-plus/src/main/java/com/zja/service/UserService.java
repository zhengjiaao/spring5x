package com.zja.service;

import com.github.pagehelper.PageInfo;
import com.zja.entity.UserEntity;

import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/14
 */
public interface UserService {

    //静态插入数据:通用方法
    int insertUser(UserEntity userEntity);

    //动态插入数据: mysql用法，id自增
    int insertUserMysql(UserEntity userEntity);
    //动态插入数据:oracle用法，id使用序列
    int insertUserOracle(UserEntity userEntity);

    //mybatis批量插入数据:mysql用法，id自增
    int mysqlBatchSaveUser(List<UserEntity> userEntities);
    //mybatis批量插入数据:oracle用法，id使用序列
    int oracleBatchSaveUser(List<UserEntity> userEntities);

    //按id查询用户
    UserEntity queryUserById(Long id);
    //查询所有用户
    List<UserEntity> queryAllUser();
    //按用户查询用户信息
    List<UserEntity> queryByUserName(String userName);

    //获取分页结果
    List<UserEntity> getPagingResults(int pageNum, int pageSize);
    //获取分页结果及分页信息
    PageInfo<UserEntity> queryPageInfo(int pageNum, int pageSize);

    //更新数据-改数据
    int updateUser(UserEntity userEntity);

    //删除数据
    int delUser(Integer id);


}
