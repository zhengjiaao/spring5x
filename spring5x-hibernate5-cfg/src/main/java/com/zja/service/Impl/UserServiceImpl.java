package com.zja.service.Impl;

import com.zja.entity.UserEntity;
import com.zja.service.UserService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/19
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource
    private HibernateTemplate hibernateTemplate;


    /*********** save/merge存数据 *************/

    /**
     * save保存-成功
     *
     * @param userEntity
     */
    @Override
    public Object saveEntity(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.save(userEntity);
    }

    /**
     * save保存-成功
     *
     * @param userEntity
     */
    @Override
    public Object saveEntity2(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.save("userEntity", userEntity);
    }

    /**
     * merge保存-成功 :存在则更新，不存在则保存
     * 使用merge的时候，执行完成，我们提供的对象A还是脱管状态，hibernate或者new了一个B，或者检索到
     * 一个持久对象B，并把我们提供的对象A的所有的值拷贝到这个B，执行完成后B是持久状态，而我们提供的A还是托管状态
     *
     * @param userEntity 实体类
     */
    @Override
    public UserEntity mergeEntity(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.merge(userEntity);
    }

    /**
     * merge保存-成功 :存在则更新，不存在则保存
     *
     * @param userEntity 实体类
     */
    @Override
    public UserEntity mergeEntity2(UserEntity userEntity) {
        userEntity.setCreateTime(new Date());
        return hibernateTemplate.merge("userEntity", userEntity);
    }


    /*********** get/load存取单条数据 *************/
    //get和load的根本区别:
    // hibernate对于load方法认为该数据在数据库中一定存在，可以放心的使用代理来延迟加载，如果在 使用过程中发现了问题，就抛异常；
    // 对于get方法，hibernate一定要获取到真实的数据，否则返回null

    /**
     * 根据id获取用户信息-成功
     * 查询数据不存在，返回null,推荐使用, 不存在延迟加载问题，不采用lazy机制的
     *
     * @param id
     */
    @Override
    public UserEntity getEntity(Integer id) {
        return hibernateTemplate.get(UserEntity.class, id);
    }

    /**
     * load查询单条数据，查询数据不存在，报异常(noSession)，不推荐使用,存在延迟加载问题-失败
     *
     * @param id
     */
    @Override
    public UserEntity loadEntity(Integer id) {
        return hibernateTemplate.load(UserEntity.class, id);
    }

    /**
     * loadAll查询所有用户信息-成功
     *
     * @param
     */
    @Override
    public List<UserEntity> getListEntity() {
        return hibernateTemplate.loadAll(UserEntity.class);
    }

    /**
     * 根据id查询-失败
     *
     * @param id
     */
    @Override
    public UserEntity getEntityName(Integer id) {
        Object userEntity = hibernateTemplate.get("userEntity", id);
        System.out.println("userEntity: " + userEntity);
        return (UserEntity) hibernateTemplate.get("userEntity", id);
    }

    /**
     * 分页查询-不推荐用-成功
     */
    @Override
    public List<UserEntity> getPageByConditions() {
        //hibernateTemplate单例,分页会有问题,执行一次分页后,其它的所有查询都是分页后效果
        hibernateTemplate.setMaxResults(5);
        List<UserEntity> loadAll = hibernateTemplate.loadAll(UserEntity.class);
        hibernateTemplate.setMaxResults(0);
        return loadAll;
    }

    /*********** 更新 *************/

    /**
     * 更新-成功
     *
     * @param userEntity
     */
    @Override
    public void updateEntity(UserEntity userEntity) {
        hibernateTemplate.update(userEntity);
    }

    /**
     * 保存或更新：id不存在则保存，存在则更新-成功
     *
     * @param userEntity
     */
    @Override
    public void saveOrUpdate(UserEntity userEntity) {
        hibernateTemplate.saveOrUpdate(userEntity);
    }

    /*********** 删除 *************/

    /**
     * 删除单条数据-成功
     *
     * @param id
     */
    @Override
    public void deleteByEntity(Integer id) {
        hibernateTemplate.delete(getEntity(id));
    }

    /**
     * 删除全部数据-成功
     *
     * @param
     */
    @Override
    public void deleteAll() {
        List<UserEntity> userEntityList = findByCriteria();
        hibernateTemplate.deleteAll(userEntityList);
    }


    /*********** hibernateTemplate执行原生sql脚本 *************/

    /**
     * HiberateTemplate提供的方法不能满足要求时才使用execute方法 执行sql -成功
     */
    @Override
    public Object execute() {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                //创建空表mysql_ok
                session.createSQLQuery(
                        "DROP TABLE IF EXISTS `mysql_ok`;")
                        .executeUpdate();
                int i = session.createSQLQuery(
                        "CREATE TABLE `mysql_ok` (`ok`  varchar(3) NOT NULL ,PRIMARY KEY (`ok`));")
                        .executeUpdate();
                System.out.println("成功创建空表mysql_ok ：i=" + i);
                return i;
            }
        });
        return o;
    }

    /**
     * 保存实体类-成功
     * @param userEntity
     */
    @Override
    public Object executeSaveUserEntity(UserEntity userEntity) {
        Object o = this.hibernateTemplate.execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                //创建空表mysql_ok
                Serializable save = session.save(userEntity);
                System.out.println("id=" + save);
                return save;
            }
        });
        return o;
    }

    /*********** findByExample查询数据 *************/

    /**
     * 根据条件查询-成功
     */
    @Override
    public List<UserEntity> findByExample(Integer age,String userName) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setAge(age);
        //必须 符合的条件但是这 "username=小刘 and age=20" 两个条件时并列的（象当于sql中的and)
        return hibernateTemplate.findByExample(userEntity);
    }

    /**
     * 根据调条件查询并分页-成功
     *
     * @param firstResult 页码 从0开始
     * @param maxResults  每页显示多少条数据，等于0或负数则查询所有
     */
    @Override
    public List<UserEntity> findByExample(int firstResult, int maxResults) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("小刘");
        userEntity.setAge(20);
        return hibernateTemplate.findByExample(userEntity, firstResult, maxResults);
    }

    /*********** findByExample查询数据 *************/

    /**
     * 查询所有数据 -成功
     */
    @Override
    public List<UserEntity> findByCriteria() {
        //DetachedCriteria：离线条件查询对象，该对象的创建不需要session对象
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria);
    }

    /**
     * 分页查询-成功
     *
     * @param firstResult 页码 从0开始
     * @param maxResults  每页显示多少条数据，等于0或负数则查询所有
     */
    @Override
    public List<UserEntity> findPageByCriteria(int firstResult, int maxResults) {
        //firstResult 第几页，maxResults每页显示多少条
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }

    /**
     * 根据属性名称和属性值查询-成功
     *
     * @param propertyName  类的属性名称
     * @param propertyValue 属性值
     * @param firstResult   页码 从0开始
     * @param maxResults    每页显示多少条数据，等于0或负数则查询所有
     */
    @Override
    public List<UserEntity> findByCriteria(String propertyName, Object propertyValue, int firstResult, int maxResults) {
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        //根据属性名称和属性的value值查询
        criteria.add(Restrictions.eq(propertyName, propertyValue));
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }

    /**
     * 模糊查询按年龄区间和大小排序-成功
     *
     * @param low  低
     * @param high 高  low --> high 从低到高，顺序一定要正确，否则查询会有问题，或查询不到数据
     */
    @Override
    public List<UserEntity> findByCriteria(int low, int high) {
        //
        DetachedCriteria criteria = DetachedCriteria.forClass(UserEntity.class);
        //根据属性名称和属性的value值查询
        criteria.add(Restrictions.like("userName", "小%"));
        criteria.add(Restrictions.between("age", low, high));
        //根据属性名称进行排序
        criteria.addOrder(Order.desc("age"));
        return (List<UserEntity>) hibernateTemplate.findByCriteria(criteria);
    }
}