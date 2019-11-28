package com.zja.entity.manytoMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Set;

/**
 * 学生
 * Hibernate获取数据java.lang.StackOverflowError 原因：因为在重写toString()方法时，把关联的属性也放入到toString方法中了，去掉就可以了
 */
public class Student {
	private int stuId;
	private String stuName;
	//老师集合
	private Set<Teacher> teachers;

	public Student() {
	}

	public int getStuId() {
		return stuId;
	}

	public void setStuId(int stuId) {
		this.stuId = stuId;
	}

	public String getStuName() {
		return stuName;
	}

	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	@JsonBackReference
	public Set<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(Set<Teacher> teachers) {
		this.teachers = teachers;
	}
}