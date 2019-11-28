package com.zja.service.Impl;

import com.zja.entity.UserEntity;
import com.zja.rowmapper.UserEntityRowMapper;
import com.zja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-28 15:59
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 增加用户(将用户保存到数据库)
     *
     * @param
     */
    @Override
    public Object saveUser(String username, int age) {
        UserEntity userByName = (UserEntity) this.findUserByName(username);
        if (userByName == null) {
            String sql = "insert into userentity (username,age) values(?,?)";
            Object[] args = {username, age};
            int insert = this.jdbcTemplate.update(sql, args);
            if (insert > 0) {
                return "新增用户成功!!!";
            }
        }
        return "用户已存在,请改用其它用户名称!";
    }

    /**
     * 根据用户名称修改用户年龄(修改库中用户信息)
     *
     * @param
     */
    @Override
    public Object updateUser(String username, int age) {
        UserEntity userByName = (UserEntity) this.findUserByName(username);
        if (userByName != null) {
            //sql语句
            String sql = "update userentity  SET age = ? WHERE username = ?";
            Object[] args = {age, username};
            int update = jdbcTemplate.update(sql, args);
            if (update > 0) {
                return "修改用户年龄成功!!!";
            }
        }
        return "用户名称不存在,修改年龄失败!";
    }

    /**
     * 根据用户名查询用户信息(从数据库中获取用户信息)
     *
     * @param username 用户名称
     */
    @Override
    public Object findUserByName(String username) {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity where username=?", new BeanPropertyRowMapper<UserEntity>(UserEntity.class), username);
        if (query.isEmpty()) {
            return null;
        } else if (query.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        } else {
            return query.get(0);
        }
    }

    /**
     * 查询所有用户信息
     */
    @Override
    public Object getAllUser() {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity", new BeanPropertyRowMapper<UserEntity>(UserEntity.class));
        if (query.isEmpty()) {
            return null;
        }else {
            return query.get(0);
        }
    }

    /**
     * 根据用户名查询用户信息(从数据库中获取用户信息)
     *
     * @param username 用户名称
     */
    @Override
    public Object findUserByName2(String username) {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity where username=?", new UserEntityRowMapper(), username);
        if (query.isEmpty()) {
            return null;
        } else if (query.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        } else {
            return query.get(0);
        }
    }

    /**
     * 查询所有用户信息
     */
    @Override
    public Object getAllUser2() {
        List<UserEntity> query = jdbcTemplate.query("select * from userentity", new UserEntityRowMapper());
        if (query.isEmpty()) {
            return null;
        }else {
            return query.get(0);
        }
    }

    /**
     * 根据用户名称删除用户(删除数据库中用户)
     *
     * @param username 用户名称
     */
    @Override
    public Object deleteUserByName(String username) {
        String sql = "delete from userentity where username = ?";
        int update = this.jdbcTemplate.update(sql, username);
        if (update > 0) {
            return "删除成功!!!";
        }
        return "删除失败!!!";
    }

    @Override
    public Object deleteAllUser() {
        String sql = "delete from userentity";
        int update = this.jdbcTemplate.update(sql);
        if (update > 0) {
            return "删除所有用户成功!!!";
        }
        return "删除所有用户成功!!!";
    }
}
