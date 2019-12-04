package com.zja.service;

import com.zja.entity.Student;

/**
 * Date: 2019-12-02 13:19
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public interface StudentService {

    /** ******* 新增操作 ******* **/
    Object insertStudent(Student student);

    /** ******* 查询操作 ******* **/
    Object getStudentById(Integer stuId);
    Object getByStudentId(Long stuId);
    Object getOneStudent(String stuName,Integer stuId);
    Object getStudentByIdList();
    Object getStudentByMap();
    Object getStudentByPage(long current, long size);

    Object getAllStudent();

    /** ******* 更新操作 ******* **/
    Object updateStudent();

    /** ******* 删除操作 ******* **/
    Object deleteStudentById(Integer stuId);
    Object deleteStudentByColumnMap();
    Object deleteStudentByIdList();
    Object deleteStudentByWrapper(String stuName);
}
