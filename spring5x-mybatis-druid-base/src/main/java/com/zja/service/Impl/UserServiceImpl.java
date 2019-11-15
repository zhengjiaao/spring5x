package com.zja.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zja.dao.UserDao;
import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    //静态插入数据:通用方法
    @Override
    public int insertUser(UserEntity userEntity) {
        UserEntity userById = this.userDao.queryUserById(userEntity.getId());
        if (userById !=null){
            throw new RuntimeException("id自增，默认可不传id，是唯一主键，已经存在："+userEntity.getId());
        }
        userEntity.setCreateTime(new Date());
        return this.userDao.insertUser(userEntity);
    }

    //动态插入数据: mysql用法，id自增
    @Override
    public int insertUserMysql(UserEntity userEntity) {
        return this.userDao.insertUserMysql(userEntity);
    }

    //动态插入数据:oracle用法，id使用序列
    @Override
    public int insertUserOracle(UserEntity userEntity) {
        return this.userDao.insertUserOracle(userEntity);
    }

    //mybatis批量插入数据:mysql用法，id自增
    @Override
    public int mysqlBatchSaveUser(List<UserEntity> userEntities) {
        return this.userDao.mysqlBatchSaveUser(userEntities);
    }

    //mybatis批量插入数据:oracle用法，id使用序列
    @Override
    public int oracleBatchSaveUser(List<UserEntity> userEntities) {
        return this.userDao.oracleBatchSaveUser(userEntities);
    }

    //按id查询用户
    @Override
    public UserEntity queryUserById(Integer id) {
        return this.userDao.queryUserById(id);
    }

    //查询所有用户
    @Override
    public List<UserEntity> queryAllUser() {
        return this.userDao.queryAllUser();
    }

    /**
     * 获取分页结果
     * @param pageNum 页码值
     * @param pageSize 每页显示条数
     */
    @Override
    public List<UserEntity> getPagingResults(int pageNum, int pageSize) {
        //第一个参数是页码值，第二个参数是每页显示条数，第三个参数默认查询总数count
        //获取第pageNum页，每页pageSize条内容，默认查询总数count
        PageHelper.startPage(pageNum, pageSize);
        //紧跟着的第一个select方法会被分页
        return this.userDao.queryAllUser();
    }

    /**
     * 获取分页结果及分页信息
     * @param pageNum 页码值
     * @param pageSize 每页显示条数
     */
    @Override
    public PageInfo<UserEntity> queryPageInfo(int pageNum, int pageSize) {
        //第一个参数是页码值，第二个参数是每页显示条数，第三个参数默认查询总数count
        //获取第pageNum页，每页pageSize条内容，默认查询总数count
        PageHelper.startPage(pageNum, pageSize,true);
        //紧跟着的第一个select方法会被分页
        List<UserEntity> userEntities = this.userDao.queryAllUser();

        //分页信息
        PageInfo<UserEntity> pageInfo = new PageInfo<UserEntity>(userEntities);

        //打印分页信息
        System.out.println("数据总数：" + pageInfo.getTotal());
        System.out.println("数据总页数：" + pageInfo.getPages());
        System.out.println("进入第一页：" + pageInfo.getNavigateFirstPage());
        System.out.println("进入最后一页：" + pageInfo.getNavigateLastPage());

        for (UserEntity user : pageInfo.getList()) {
            System.out.println(user);
        }
        return pageInfo;
    }

    //更新数据-改数据
    @Override
    public int updateUser(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return this.userDao.updateUser(userEntity);
    }

    //删除数据
    @Override
    public int delUser(Integer id) {
        return this.userDao.delUser(id);
    }

}
