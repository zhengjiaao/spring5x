package com.zja.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;

/**
 * @author ZhengJa
 * @description 解决mybatis 使用懒加载返回json 报错:SerializationFeature.FAIL_ON_EMPTY_BEANS
 * @data 2019/11/18
 */
public class MyObjectMapper extends ObjectMapper {
    public MyObjectMapper(){
        //设置null值不参与序列化(字段不被显示)
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 禁用 空对象转换json校验
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 禁用 忽略未知的字段
        //this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }
}
