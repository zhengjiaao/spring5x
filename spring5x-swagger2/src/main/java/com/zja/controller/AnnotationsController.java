package com.zja.controller;

import com.zja.entity.DemoEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhengJa
 * @description spring MVC 注解
 * @data 2019/10/24
 */
@RestController
@RequestMapping("rest/annotations")
public class AnnotationsController {

    /* *****************无参请求******************** */

    @GetMapping("get/test")
    public String getTest() {
        return "这是getTest";
    }

    @PostMapping("post/test")
    public String postTest() {
        return "这是postTest";
    }

    @DeleteMapping("delete/test")
    public String deleteTest() {
        return "这是deleteTest";
    }

    @PutMapping("put/test")
    public String putTest() {
        return "这是putTest";
    }

    /* *****************有参请求 str 类型 ******************** */

    @GetMapping("get/test2")
    public String getTest2(@RequestParam String name,
                           @RequestParam String value) {
        System.out.println("name= " + name);
        return "这是getTest2";
    }

    @PostMapping("post/test2")
    public String postTest2(@RequestBody String name) {
        System.out.println("name= " + name);
        return "这是postTest2";
    }

    /* *****************有参请求 entity 类型 ******************** */

    @RequestMapping(value = "post/test3", method = RequestMethod.POST)
    public String postTest3(@RequestBody DemoEntity demoEntity) {
        System.out.println("post-demoEntity= " + demoEntity);
        return "这是postTest3";
    }


}
