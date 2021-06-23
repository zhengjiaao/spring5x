package com.zja.service;

import com.zja.entity.UserEntity;

import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/19
 */
public interface UserService {

    Object saveEntity(UserEntity userEntity);

    Object saveEntity2(UserEntity userEntity);

    UserEntity mergeEntity(UserEntity userEntity);

    UserEntity mergeEntity2(UserEntity userEntity);

    Object getEntity(Integer id);

    Object loadEntity(Integer id);

    Object getListEntity();

    UserEntity getEntityName(Integer id);

    List<UserEntity> getPageByConditions();

    void updateEntity(UserEntity userEntity);

    void saveOrUpdate(UserEntity userEntity);

    void deleteByEntity(Integer id);

    void deleteAll();

    Object execute();

    Object executeSaveUserEntity(UserEntity userEntity);

    List<UserEntity> findByExample(Integer age,String userName);

    List<UserEntity> findByExample(int firstResult, int maxResults);

    List<UserEntity> findByCriteria();

    List<UserEntity> findPageByCriteria(int firstResult, int maxResults);

    List<UserEntity> findByCriteria(String propertyName, Object value, int firstResult, int maxResults);

    List<UserEntity> findByCriteria(int low, int high);

}
