package com.zja.controller;

import com.zja.entity.Programmer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author ZhengJa
 * @description 参数绑定测试
 * @data 2019/10/22
 */
@Api(value = "ParamBindController")
@Controller
public class ParamBindController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    }

    @ApiOperation(value = "param-参数绑定与日期格式转换", httpMethod = "GET")
    @RequestMapping(value = "param", method = RequestMethod.GET)
    public String param(@ApiParam("name") @RequestParam String name,
                        @ApiParam("age") @RequestParam int age,
                        @ApiParam("salary") @RequestParam double salary,
                        @ApiParam(value = "birthday",defaultValue = "2019-10-22 09:02:02") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date birthday,
                        Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        model.addAttribute("salary", salary);
        model.addAttribute("birthday", birthday);
        return "param";
    }

    @ApiOperation(value = "param2-参数绑定与日期格式转换", httpMethod = "GET")
    @RequestMapping(value = "param2", method = RequestMethod.GET)
    public String param2(@ApiParam("name") @RequestParam String name,
                         @ApiParam("age") @RequestParam int age,
                         @ApiParam("salary") @RequestParam double salary,
                         @ApiParam(value = "birthday",defaultValue = "2019-10-22 09:02:02") @RequestParam Date birthday,
                         Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        model.addAttribute("salary", salary);
        model.addAttribute("birthday", birthday);
        return "param";
    }

    @PostMapping("param3")
    public String param3(Programmer programmer,
                         String extendParam, Model model) {
        System.out.println("extendParam" + extendParam);
        model.addAttribute("p", programmer);
        return "param";
    }

}
