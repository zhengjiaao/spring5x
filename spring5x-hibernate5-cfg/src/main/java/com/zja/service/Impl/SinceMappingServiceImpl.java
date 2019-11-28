package com.zja.service.Impl;

import com.zja.entity.sinceMapping.Menu;
import com.zja.service.SinceMappingService;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-26 13:51
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Service
public class SinceMappingServiceImpl implements SinceMappingService {

    @Resource
    private HibernateTemplate hibernateTemplate;

    @Override
    public Object getAllMenu(){
        return this.hibernateTemplate.loadAll(Menu.class);
    }

    @Override
    public Object getMenu(int menuId){
        Menu menu = this.hibernateTemplate.get(Menu.class, menuId);
        return menu;
    }

    @Override
    public Object saveMenu(){
        Menu menu = new Menu();
        menu.setMenuName("根节点");

        Menu menu1 = new Menu();
        menu1.setMenuName("一级菜单");
        menu1.setParentMenu(menu);

        Menu childMenu1 = new Menu();
        childMenu1.setMenuName("一级菜单1子菜单1");
        childMenu1.setParentMenu(menu1);

        Menu childMenu2 = new Menu();
        childMenu2.setMenuName("一级菜单1子菜单2");
        childMenu2.setParentMenu(menu1);

        this.hibernateTemplate.save(menu);
        this.hibernateTemplate.save(menu1);
        this.hibernateTemplate.save(childMenu1);
        this.hibernateTemplate.save(childMenu2);
        return "成功";
    }
}
