package com.zja.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zja.dao.StudentMapper;
import com.zja.entity.Student;
import com.zja.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2019-12-02 13:19
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentDao;

    /** ******* 入库(新增)操作 ******* **/

    /**
     * 插入数据-失败
     * @param student
     */
    @Override
    public Object insertStudent(Student student){
        //mybatisplus会自动把当前插入对象在数据库中的id写回到该实体中
        int insert = studentDao.insert(student);
        return insert;
    }

    /** ******* 查询操作 ******* **/

    /**
     * 1.通过id查询
     * @param stuId id
     */
    @Override
    public Object getStudentById(Integer stuId){
        //SELECT stuid,stuname,stuage,email FROM student WHERE stuid=?
        return studentDao.selectById(stuId);
    }

    /**
     * 2.根据对象查询
     * @param stuId
     */
    @Override
    public Object getByStudentId(Long stuId){
        Student student = new Student();
        student.setStuId(stuId);
        //SELECT stuid,stuname,stuage,email FROM student WHERE stuid=?
        return studentDao.selectById(student);
    }

    /**
     * 2.通过多个列进行查询，根据条件查询一条数据，返回多条结果会报异常 -失败
     * @param stuName
     * @param stuId
     */
    @Override
    public Object getOneStudent(String stuName,Integer stuId){
        Student student = new Student();
        student.setStuId(1L);
        student.setStuName("学生1");
        student.selectById(stuId);
        //若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        //select * from student where id = ？ and stuname = ？
        return this.studentDao.selectOne(new QueryWrapper<Student>().select());
    }

    /**
     * 3. 通过多个id进行查询    <foreach>
     * @param
     */
    @Override
    public Object getStudentByIdList(){
        List<Integer> idList = new ArrayList<>();
        idList.add(1);
        idList.add(2);
        idList.add(3);
        //SELECT stuid,stuname,stuage,email FROM student WHERE stuid IN ( ? , ? , ? )
        List<Student> students = studentDao.selectBatchIds(idList);
        return students;
    }

    /**
     * 4.通过多个列进行查询
     */
    @Override
    public Object getStudentByMap(){
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("stuname", "李四");
        columnMap.put("stuage", 20);
        List<Student> students = studentDao.selectByMap(columnMap);
        return students;
    }

    /**
     * 5.分页查询 根据ROWNUM分页查询
     * @param current 当前页
     * @param size 每页显示多少条
     */
    @Override
    public Object getStudentByPage(long current, long size){
        //1.SELECT COUNT(1) FROM ( SELECT stuid,stuname,stuage,email FROM student ) TOTAL
        //2.SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( SELECT stuid,stuname,stuage,email FROM student ) TMP WHERE ROWNUM <=?)
        IPage<Student> studentIPage = studentDao.selectPage(new Page<>(current, size), null);
        return studentIPage;
    }

    /**
     * 多条件查询
     */
    public Object getByManyCondition(){
        //
        return studentDao.selectList(new QueryWrapper<Student>().eq("stuname","学生")
                .between("stuage","20","23"));
    }

    /**
     * 6.查询所有数据
     */
    @Override
    public Object getAllStudent(){
        //可传null，查询所有
        List<Student> studentList = studentDao.selectList(null);
        return studentList;
    }

    /** ******* 更新操作 ******* **/

    @Override
    public Object updateStudent(){
        Student student = new Student();
        student.setStuId(1L);
        student.setStuName("艾莉娅-更新");

        //根据id进行更新，没传值的属性就更新为null
        //studentDao.update(student,updateWrapper);

        //updateById 根据id进行更新，没有传值的属性就不会更新,注意 基本数据类型可能会使用默认值，如：int默认0会覆盖表中的值,推荐使用基本数据类型的包装类
        //UPDATE student SET stuname=?, stuage=? WHERE stuid=?  #并没有传stuage int类型的值，但是使用默认值
        int i = studentDao.updateById(student);
        return i;
    }

    /** ******* 删除操作 ******* **/

    /**
     * 1.通用删除操作  通过ID删除
     * @param stuId
     */
    @Override
    public Object deleteStudentById(Integer stuId){
        //DELETE FROM student WHERE stuid=?
        int i = studentDao.deleteById(stuId);
        return i;
    }

    /**
     * 2.通用删除操作 deleteByMap  map要写表的列名条件 不能是实体属性名
     */
    @Override
    public Object deleteStudentByColumnMap(){
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("stuname", "李四");
        columnMap.put("stuage", 20);
        //DELETE FROM student WHERE stuname = ? AND stuage = ?
        int i = studentDao.deleteByMap(columnMap);
        return i;
    }

    /**
     * 3.通用删除操作 deleteBatchIds 通过多个ID进行删除
     */
    @Override
    public Object deleteStudentByIdList(){
        List<Integer> idList = new ArrayList<>();
        idList.add(4);
        idList.add(5);
        idList.add(6);
        //DELETE FROM student WHERE stuid IN ( ? , ? , ? )
        int i = studentDao.deleteBatchIds(idList);
        return i;
    }

    /**
     * 4.根据条件构造器删除
     */
    @Override
    public Object deleteStudentByWrapper(String stuName){
        //DELETE FROM student WHERE (stuname = ?)
        int i = studentDao.delete(new QueryWrapper<Student>().eq("stuname",stuName));
        return i;
    }

}
