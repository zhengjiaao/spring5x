package com.zja.controller;

import com.zja.service.OneToOneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-11-26 13:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest/onetoone")
@Api(tags = {"OneToOneController"}, description = "一对一主键关联（双向）")
public class OneToOnePkController {

    @Autowired
    private OneToOneService oneToOneService;

    /** ******************一对一主键关联（双向）****************** **/

    @PostMapping("savepk")
    @ApiOperation(value = "1、模拟数据入库", notes = "一对一主键关联（双向）", httpMethod = "POST")
    public Object savepk() {
        return this.oneToOneService.savepk();
    }

    @GetMapping("getUserPkById")
    @ApiOperation(value = "2、根据userId获取用户信息", notes = "一对一主键关联（双向）", httpMethod = "GET")
    public Object getUserPkById(@ApiParam(defaultValue = "1") @RequestParam int userId) {
        return this.oneToOneService.getUserPkById(userId);
    }

    @GetMapping("getResumePkById")
    @ApiOperation(value = "3、根据resId获取档案信息", notes = "一对一主键关联（双向）", httpMethod = "GET")
    public Object getResumePkById(@ApiParam(defaultValue = "1") @RequestParam int resId) {
        return this.oneToOneService.getResumePkById(resId);
    }

    @GetMapping("getAllUserPk")
    @ApiOperation(value = "4、获取所有用户信息", notes = "一对一主键关联（双向）", httpMethod = "GET")
    public Object getAllUserPk() {
        return this.oneToOneService.getAllUserPk();
    }

    @GetMapping("getAllResumePk")
    @ApiOperation(value = "5、获取所有档案信息", notes = "一对一主键关联（双向）", httpMethod = "GET")
    public Object getAllResumePk() {
        return this.oneToOneService.getAllResumePk();
    }
}
