package com.zja.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-12-12 16:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 * @author Administrator
 */
@Data
@Entity
@Table(name = "demojpa")
public class DemoJpa implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column
    private String email;
    private int age;

}
