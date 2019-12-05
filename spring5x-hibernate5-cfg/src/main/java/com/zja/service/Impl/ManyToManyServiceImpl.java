package com.zja.service.Impl;

import com.zja.model.dto.manytoMany.StudentDTO;
import com.zja.model.dto.manytoMany.TeacherDTO;
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

        TeacherDTO teacherDTO =new TeacherDTO();
        teacherDTO.setTeaId(teacher.getTeaId());
        teacherDTO.setTeaName(teacher.getTeaName());

        //延迟加载：当获取老师有哪些学生时会二次sql查询学生信息
        Set<Student> students = teacher.getStudents();
        Set<StudentDTO> studentDTOS = new HashSet<>();
        if (students !=null ){
            Iterator<Student> iterator = students.iterator();
            while (iterator.hasNext()){
                Student student = iterator.next();
                System.out.println("老师【 "+teacher.getTeaName()+"】的学生有: "+student.getStuName());

                StudentDTO studentDTO = new StudentDTO();
                studentDTO.setStuId(student.getStuId());
                studentDTO.setStuName(student.getStuName());

                studentDTOS.add(studentDTO);
            }
        }
        teacherDTO.setStudents(studentDTOS);

        //使用DTO解决嵌套死循环问题
        return teacherDTO;
        //使用@JsonBackReference 在集合的get方法上，解决死循环问题，但不是完美解决
        //return teacher;
    }

    @Override
    public Object getAllTeacher(){
        return this.hibernateTemplate.loadAll(Teacher.class);
    }

    @Override
    public Object getStudent(int stuId){
        Student student = this.hibernateTemplate.get(Student.class, stuId);
        System.out.println("StuName: "+student.getStuName());

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStuId(student.getStuId());
        studentDTO.setStuName(student.getStuName());
        //延迟加载：当获取学生属于哪个老师时会二次sql查询老师信息
        Set<Teacher> teachers = student.getTeachers();
        Iterator<Teacher> iterator = teachers.iterator();
        Set<TeacherDTO> teacherDTOS = new HashSet<>();
        while (iterator.hasNext()){
            Teacher teacher = iterator.next();
            System.out.println("学生【 "+student.getStuName()+" 】的老师: "+teacher.getTeaName());

            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setTeaId(teacher.getTeaId());
            teacherDTO.setTeaName(teacher.getTeaName());
            teacherDTOS.add(teacherDTO);

        }
        studentDTO.setTeachers(teacherDTOS);

        //使用DTO解决嵌套死循环问题
        return studentDTO;
        //使用@JsonBackReference 在集合的get方法上，解决死循环问题，但不是完美解决
        //return student;
    }

    @Override
    public Object getAllStudent(){
        return this.hibernateTemplate.loadAll(Student.class);
    }

}
