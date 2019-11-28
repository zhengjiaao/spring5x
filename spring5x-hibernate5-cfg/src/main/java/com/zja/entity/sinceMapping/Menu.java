package com.zja.entity.sinceMapping;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-25 13:24
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：Hibernate5 映射之自身关联
 */
@Data
@NoArgsConstructor
public class Menu implements Serializable {
    private int menuId;
    private String menuName;
    // 菜单和子菜单是一对多，子引入父类对象
    private Menu parentMenu;
    // 这里引入子类对象集合
    private Set<Menu> childMenus = new HashSet<>();
}
