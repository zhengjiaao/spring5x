package com.zja.entity;

import lombok.Data;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Date: 2019-12-18 9:48
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Data
@Entity
@Table(name="vuserorder")
@Subselect("select u.user_id,u.user_name,o.order_id,o.address_id"+
        " from t_user u left join t_order o on u.user_id=o.user_id")
@Synchronize({"t_user","t_order"})
public class VUserOrder {

    @Id
    @Column(name = "user_id")
    private long userId;
    @Column(name = "user_name")
    private String userName;

    @Column(name = "order_id")
    private long orderId;
    @Column(name = "address_id")
    private long addressId;
}
