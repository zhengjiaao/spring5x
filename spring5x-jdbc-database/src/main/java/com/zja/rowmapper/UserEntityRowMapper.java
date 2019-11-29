package com.zja.rowmapper;

import com.zja.entity.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 封装返回结果(推荐此方法)
 */
public class UserEntityRowMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(resultSet.getLong("id"));
        userEntity.setUserName(resultSet.getString("username"));
        userEntity.setAge(resultSet.getInt("age"));
        return userEntity;
    }
}
