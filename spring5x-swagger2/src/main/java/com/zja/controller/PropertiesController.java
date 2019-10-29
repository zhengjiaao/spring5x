package com.zja.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description properties 项目多环境测试
 * @data 2019/10/24
 */
@RestController
@RequestMapping("rest/properties")
public class PropertiesController {

    @Value("${environment}")
    private String environment;

    @GetMapping("get/test")
    public String getTest() {
        return "这是【 " + environment + " 】" +"环境";
    }

}
