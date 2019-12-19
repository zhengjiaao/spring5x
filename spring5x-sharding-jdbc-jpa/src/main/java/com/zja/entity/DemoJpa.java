package com.zja.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Date: 2019-12-12 16:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Data
@Entity
@Table(name = "demojpa")
public class DemoJpa implements Serializable {

    /**默认 序列名称 HIBERNATE_SEQUENCE
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * 主键生成策略：1.AUTO 由程序控制 2.SEQUENCE 由序列，数据库支持（如oracle）
     * 3.IDENTITY 主键由数据库自动生成（主要是自动增长型如mysql） 4.TABLE：使用一个特定的数据库表格来保存主键
     *
     * 注意：第一次启动项目，mysql数据库jpa自动创建默认hibernate_sequence序列，
     *      oracle不自动创建默认序列，需要手动创建序列HIBERNATE_SEQUENCE
     */
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue
    private int id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column
    private String email;
    private int age;

}
