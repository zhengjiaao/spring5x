package com.zja.service;

/**
 * Date: 2019-11-29 14:16
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface OneToOneService {

    /** ******************一对一外键关联（双向）****************** **/

    Object save();

    Object getAllUserOne();
    Object getUserOneById(int userId);

    Object getAllResume();
    Object getResumeById(int resId);


    /** ******************一对一主键关联（双向）****************** **/

    Object savepk();

    Object getAllUserPk();
    Object getUserPkById(int userId);

    Object getAllResumePk();
    Object getResumePkById(int resId);

}
