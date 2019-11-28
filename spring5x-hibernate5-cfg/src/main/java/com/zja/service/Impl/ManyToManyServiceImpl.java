package com.zja.service.Impl;

import com.zja.entity.manytoMany.Student;
import com.zja.entity.manytoMany.Teacher;
import com.zja.service.ManyToManyService;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-26 14:35
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Service
public class ManyToManyServiceImpl implements ManyToManyService {

    @Resource
    private HibernateTemplate hibernateTemplate;

    @Override
    public Object saveTeacherAndStudent(){
        Teacher teacher1 = new Teacher();
        teacher1.setTeaName("老师1");
        Teacher teacher2 = new Teacher();
        teacher2.setTeaName("老师2");

        Student student1 = new Student();
        student1.setStuName("学生1");
        Student student2 = new Student();
        student2.setStuName("学生2");

        Set<Teacher> teachers = new HashSet<>();
        teachers.add(teacher1);
        teachers.add(teacher2);
        // 学生1有俩个老师
        student1.setTeachers(teachers);

        this.hibernateTemplate.save(teacher1);
        this.hibernateTemplate.save(teacher2);
        this.hibernateTemplate.save(student1);
        this.hibernateTemplate.save(student2);

        return "成功";
    }

    @Override
    public Object getTeacher(int teaId){
        Teacher teacher = this.hibernateTemplate.get(Teacher.class, teaId);
        System.out.println("TeaName: "+teacher.getTeaName());

        //延迟加载：当获取老师有哪些学生时会二次sql查询学生信息
        Set<Student> students = teacher.getStudents();
        if (students !=null ){
            Iterator<Student> iterator = students.iterator();
            while (iterator.hasNext()){
                Student student = iterator.next();
                System.out.println("老师【 "+teacher.getTeaName()+"】的学生有: "+student.getStuName());
            }
        }
        return teacher;
    }

    @Override
    public Object getAllTeacher(){
        return this.hibernateTemplate.loadAll(Teacher.class);
    }

    @Override
    public Object getStudent(int stuId){
        Student student = this.hibernateTemplate.get(Student.class, stuId);
        System.out.println("StuName: "+student.getStuName());

        //延迟加载：当获取学生属于哪个老师时会二次sql查询老师信息
        Set<Teacher> teachers = student.getTeachers();
        Iterator<Teacher> iterator = teachers.iterator();
        while (iterator.hasNext()){
            Teacher teacher = iterator.next();
            System.out.println("学生【 "+student.getStuName()+" 】的老师: "+teacher.getTeaName());
        }
        return student;
    }

    @Override
    public Object getAllStudent(){
        return this.hibernateTemplate.loadAll(Student.class);
    }

}
