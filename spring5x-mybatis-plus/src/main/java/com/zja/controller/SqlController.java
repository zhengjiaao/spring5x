package com.zja.controller;

import com.zja.util.db.SchemaHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2019-12-04 15:48
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RestController
@RequestMapping("rest")
@Api(tags = {"SqlController"}, description = "执行Sql文件")
public class SqlController {

    @Autowired
    private SchemaHandler schemaHandler;

    @GetMapping("execute/sql")
    @ApiOperation(value = "执行sql文件", notes = "数据准备", httpMethod = "GET")
    public Object executeSql() throws Exception {
        schemaHandler.execute();
        return "Sql文件数据准备成功!!!";
    }

}
