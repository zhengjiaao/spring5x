package com.zja.controller;

import com.zja.entity.Programmer;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ZhengJa
 * @description 校验参数数据
 * @data 2019/10/22
 */
@RestController
public class ParamValidController {

    @PostMapping("validate")
    public void valid(@RequestBody @Validated Programmer programmer, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            System.out.println(error.getDefaultMessage());
        }
    }
}
