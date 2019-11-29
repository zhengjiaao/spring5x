package com.zja.controller;

import com.zja.service.SinceMappingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-11-26 13:47
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/since")
@Api(tags = {"SinceMappingController"}, description = "关联关系之自关联(自映射)")
public class SinceMappingController {

    @Autowired
    private SinceMappingService sinceMappingService;

    @GetMapping("getMenu")
    @ApiOperation(value = "获取单个菜单", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getMenu(@RequestParam int menuId) {
        return this.sinceMappingService.getMenu(menuId);
    }

    @GetMapping("getAllMenu")
    @ApiOperation(value = "获取所有菜单", notes = "HibernateTemplate", httpMethod = "GET")
    public Object getAllMenu() {
        return this.sinceMappingService.getAllMenu();
    }

    @PostMapping("saveMenu")
    @ApiOperation(value = "保存菜单", notes = "HibernateTemplate", httpMethod = "POST")
    public Object saveMenu() {
        return this.sinceMappingService.saveMenu();
    }

}
