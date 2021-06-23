package com.zja.controller;

import com.zja.service.OneToOneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-11-29 15:33
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/onetoone")
@Api(tags = {"OneToOnePkController"}, description = "一对一外键关联（双向）")
public class OneToOneController {

    @Autowired
    private OneToOneService oneToOneService;

    /** ******************一对一外键关联（双向）****************** **/

    @PostMapping("save")
    @ApiOperation(value = "1、模拟数据入库", notes = "一对一外键关联（双向）", httpMethod = "POST")
    public Object save() {
        return this.oneToOneService.save();
    }

    @GetMapping("getUserOneById")
    @ApiOperation(value = "2、根据userId获取用户信息", notes = "一对一外键关联（双向）", httpMethod = "GET")
    public Object getUserOneById(@ApiParam(defaultValue = "1") @RequestParam int userId) {
        return this.oneToOneService.getUserOneById(userId);
    }

    @GetMapping("getResumeById")
    @ApiOperation(value = "3、根据resId获取档案信息", notes = "一对一外键关联（双向）", httpMethod = "GET")
    public Object getResumeById(@ApiParam(defaultValue = "1") @RequestParam int resId) {
        return this.oneToOneService.getResumeById(resId);
    }

    @GetMapping("getAllUserOne")
    @ApiOperation(value = "4、获取所有用户信息", notes = "一对一外键关联（双向）", httpMethod = "GET")
    public Object getAllUserOne() {
        return this.oneToOneService.getAllUserOne();
    }

    @GetMapping("getAllResume")
    @ApiOperation(value = "5、获取所有档案信息", notes = "一对一外键关联（双向）", httpMethod = "GET")
    public Object getAllResume() {
        return this.oneToOneService.getAllResume();
    }

}
