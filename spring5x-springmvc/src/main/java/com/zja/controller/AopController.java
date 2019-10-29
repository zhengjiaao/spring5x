package com.zja.controller;

import com.zja.entity.AopEntiry;
import com.zja.service.AopService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description AOP 测试
 * @data 2019/10/28
 */
@Api(value = "AopController")
@RestController
@RequestMapping("rest/aop")
public class AopController {

    @Autowired
    AopService aopService;

    @GetMapping("find/data")
    public Object findData(AopEntiry aopEntiry){
        return aopService.findData(aopEntiry);
    }

}
