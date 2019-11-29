package com.zja.service;

/**
 * Date: 2019-11-26 14:35
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface ManyToManyService {
    //插入模拟数据
    Object saveTeacherAndStudent();
    //根据id获取老师信息
    Object getTeacher(int teaId);
    //获取所有老师信息
    Object getAllTeacher();
    //根据id获取学生信息
    Object getStudent(int stuId);
    //获取所有学生信息
    Object getAllStudent();
}
