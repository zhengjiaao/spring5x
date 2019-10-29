package com.zja.controller;

import com.zja.entity.DemoEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhengJa
 * @description spring 集成swagger测试
 * @data 2019/10/24
 */
@Api(value = "web-SwaggerController")
@RestController
@RequestMapping("rest/swagger")
public class SwaggerController {

    /* *****************无参请求******************** */

    @ApiOperation(value = "接口功能说明：查询接口", notes = "注释：get无参请求", httpMethod = "GET")
    @GetMapping("get/test")
    public String getTest() {
        return "这是getTest";
    }

    @ApiOperation(value = "保存接口", httpMethod = "POST")
    @PostMapping("post/test")
    public String postTest() {
        return "这是postTest";
    }

    @ApiOperation(value = "删除接口", notes = "这是删除请求", httpMethod = "DELETE")
    @DeleteMapping("delete/test")
    public String deleteTest() {
        return "这是deleteTest";
    }

    @ApiOperation(value = "更新接口", notes = "这是数据更新接口", httpMethod = "PUT")
    @PutMapping("put/test")
    public String putTest() {
        return "这是putTest";
    }

    /* *****************有参请求 str 类型 ******************** */

    @ApiOperation(value = "str-查询接口", notes = "str-get传参方式", httpMethod = "GET")
    @GetMapping("get/test2")
    public String getTest2(@ApiParam(value = "name参数说明", defaultValue = "get-name的默认值") @RequestParam String name,
                           @ApiParam(value = "value：必须传参", required = true) @RequestParam String value) {
        System.out.println("name= " + name);
        return "这是getTest2";
    }

    @ApiOperation(value = "str-保存接口", notes = "str-post传参方式", httpMethod = "POST")
    @PostMapping("post/test2")
    public String postTest2(@ApiParam(value = "name参数说明", defaultValue = "post-name的默认值") @RequestBody String name) {
        System.out.println("name= " + name);
        return "这是postTest2";
    }

    /* *****************有参请求 entity 类型 ******************** */

    @ApiOperation(value = "entity-保存接口", notes = "enrity-post传参方式", httpMethod = "POST")
    @RequestMapping(value = "post/test3", method = RequestMethod.POST)
    public String postTest3(@ApiParam(value = "name参数说明", defaultValue = "post-name的默认值") @RequestBody DemoEntity demoEntity) {
        System.out.println("post-demoEntity= " + demoEntity);
        return "这是postTest3";
    }


}
