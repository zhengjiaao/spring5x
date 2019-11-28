package com.zja.entity.manytoMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Set;

/**
 * 老师
 * Hibernate获取数据java.lang.StackOverflowError 原因：因为在重写toString()方法时，把关联的属性也放入到toString方法中了，去掉就可以了
 */
public class Teacher{
	private int teaId;
	private String teaName;
	// 学生集合
	private Set<Student> students;

	public Teacher() {
	}

	public int getTeaId() {
		return teaId;
	}

	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}

	public String getTeaName() {
		return teaName;
	}

	public void setTeaName(String teaName) {
		this.teaName = teaName;
	}

	@JsonBackReference
	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

}
