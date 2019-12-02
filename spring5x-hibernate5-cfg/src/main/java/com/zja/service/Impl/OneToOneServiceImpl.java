package com.zja.service.Impl;

import com.zja.entity.oneToOne.Resume;
import com.zja.entity.oneToOne.UserOne;
import com.zja.entity.oneToOnePk.ResumePk;
import com.zja.entity.oneToOnePk.UserPk;
import com.zja.service.OneToOneService;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Date: 2019-11-29 14:16
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：一对一外键关联（双向） 和 一对一主键关联（双向） 区别在于hbm.xml配置
 */
@Service
public class OneToOneServiceImpl implements OneToOneService {

    @Resource
    private HibernateTemplate hibernateTemplate;

    /** ******************一对一外键关联（双向）****************** **/

    /**
     * 默认数据入库,一对一外键关联（双向）
     */
    @Override
    public Object save(){
        UserOne userOne = new UserOne();
        userOne.setUserName("用户1");
        Resume resume = new Resume();
        resume.setResName("档案1");

        userOne.setResume(resume);

        //注意保存顺序，先保存主的一方（用户），可以减少update
        this.hibernateTemplate.save(userOne);
        this.hibernateTemplate.save(resume);

        return "模拟数据入库成功!";
    }

    /**
     * 获取所有用户信息，一对一外键关联（双向）
     */
    @Override
    public Object getAllUserOne(){
        return this.hibernateTemplate.loadAll(UserOne.class);
    }

    /**
     * 根据用户主键获取用户信息，一对一外键关联（双向）
     * @param userId 用户主键
     */
    @Override
    public Object getUserOneById(int userId){
        return this.hibernateTemplate.get(UserOne.class,userId);
    }


    /**
     * 获取所有档案，一对一外键关联（双向）
     */
    @Override
    public Object getAllResume(){
        return this.hibernateTemplate.loadAll(Resume.class);
    }

    /**
     * 根据档案主键获取用户信息，一对一外键关联（双向）
     * @param resId 用户主键
     */
    @Override
    public Object getResumeById(int resId){
        return this.hibernateTemplate.get(Resume.class,resId);
    }


    /** ******************一对一主键关联（双向）****************** **/

    /**
     * 默认数据入库，一对一主键关联（双向）
     */
    @Override
    public Object savepk(){
        UserPk userPk = new UserPk();
        userPk.setUserName("用户1");

        ResumePk resumePk = new ResumePk();
        resumePk.setResName("档案1");

        resumePk.setUser(userPk);
        userPk.setResume(resumePk);

        //注意保存顺序，先保存主的一方（用户），可以减少update
        this.hibernateTemplate.save(userPk);
        //this.hibernateTemplate.save(resumePk);

        return "模拟数据入库成功!";
    }

    /**
     * 获取所有用户信息
     */
    @Override
    public Object getAllUserPk(){
        return this.hibernateTemplate.loadAll(UserPk.class);
    }

    /**
     * 根据用户主键获取用户信息
     * @param userId 用户主键
     */
    @Override
    public Object getUserPkById(int userId){
        return this.hibernateTemplate.get(UserPk.class,userId);
    }


    /**
     * 获取所有档案
     */
    @Override
    public Object getAllResumePk(){
        return this.hibernateTemplate.loadAll(ResumePk.class);
    }

    /**
     * 根据档案主键获取用户信息
     * @param resId 用户主键
     */
    @Override
    public Object getResumePkById(int resId){
        return this.hibernateTemplate.get(ResumePk.class,resId);
    }

}
