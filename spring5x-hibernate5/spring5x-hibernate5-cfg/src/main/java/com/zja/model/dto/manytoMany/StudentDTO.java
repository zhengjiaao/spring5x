package com.zja.model.dto.manytoMany;

import lombok.Data;

import java.util.Set;

/**
 * 学生
 */
@Data
public class StudentDTO {
	private int stuId;
	private String stuName;
	//老师集合
	private Set<TeacherDTO> teachers;
}