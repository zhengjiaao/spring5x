package com.zja.convert;

import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ZhengJa
 * @description 全局时间格式
 * @data 2019/10/22
 */
public class CustomDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String s) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
