package com.zja.service.Impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zja.dao.StudentMapper;
import com.zja.entity.Student;
import com.zja.service.IStudentService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date: 2019-12-02 17:04
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Service
public class IStudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

 /*   public StudentMapper getMapper() {
        return this.baseMapper;
    }*/

    public interface StudentMapper extends BaseMapper<Student> {

        @Select("select * from student where stuname=#{stuName}")
        List<Student> selectByStuName(@Param("stuName") String stuName);
    }

}
