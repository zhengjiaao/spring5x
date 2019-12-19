package com.zja.dao;

import com.zja.entity.DemoJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Date: 2019-12-12 17:16
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface DemoJpaRepositories extends JpaRepository<DemoJpa,Integer> {

    //根据firstName与LastName查找(两者必须在数据库有)
    DemoJpa findByFirstNameAndLastName(String firstName, String lastName);

    //根据firstName或LastName查找(两者其一有就行)
    DemoJpa findByLastNameOrFirstName(String lastName,String firstName);

    //根据firstName查找它是否存在数据库里<类似与以下关键字>
    //DemoJpa findByFirstName(String firstName);
    DemoJpa findByFirstNameIs(String firstName);

    //在Age数值age到age2之间的数据
    List<DemoJpa> findByAgeBetween(Integer age, Integer age2);

    //小于指定age数值之间的数据
    List<DemoJpa> findByAgeLessThan(Integer age);

    //小于等于指定age数值的数据
    List<DemoJpa> findByAgeLessThanEqual(Integer age);

    //大于指定age数值之间的数据
    List<DemoJpa> findByAgeGreaterThan(Integer age);

    //大于或等于指定age数值之间的数据
    List<DemoJpa> findByAgeGreaterThanEqual(Integer age);

    //在指定age数值之前的数据类似关键字<LessThan>
    List<DemoJpa> findByAgeAfter(Integer age);

    //在指定age数值之后的数据类似关键字<GreaterThan>
    List<DemoJpa>  findByAgeBefore(Integer age);

    //返回age字段为空的数据
    List<DemoJpa> findByAgeIsNull();

    //返回age字段不为空的数据
    List<DemoJpa> findByAgeNotNull();

    /**
     * 该关键字我一度以为是类似数据库的模糊查询,
     * 但是我去官方文档看到它里面并没有通配符。
     * 所以我觉得它类似
     * DemoJpa findByFirstName(String firstName);
     * @see https://docs.spring.io/spring-data/jpa/docs/2.1.5.RELEASE/reference/html/#jpa.repositories
     */
    DemoJpa findByFirstNameLike(String firstName);

    //同上
    List<DemoJpa> findByFirstNameNotLike(String firstName);

    //查找数据库中指定类似的名字(如：输入一个名字"M" Jpa会返回多个包含M开头的名字的数据源)<类似数据库模糊查询>
    List<DemoJpa> findByFirstNameStartingWith(String firstName);

    //查找数据库中指定不类似的名字(同上)
    List<DemoJpa> findByFirstNameEndingWith(String firstName);

    //查找包含的指定数据源(这个与以上两个字段不同的地方在与它必须输入完整的数据才可以查询)
    List<DemoJpa> findByFirstNameContaining(String firstName);

    //根据age选取所有的数据源并按照LastName进行升序排序
    List<DemoJpa> findByAgeOrderByLastName(Integer age);

    //返回不是指定age的所有数据
    List<DemoJpa> findByAgeNot(Integer age);

    //查找包含多个指定age返回的数据
    List<DemoJpa> findByAgeIn(List<Integer> age);

}
