package com.zja.dto.manytoMany;

import lombok.Data;

import java.util.Set;

/**
 * 老师
 */
@Data
public class TeacherDTO {
	private int teaId;
	private String teaName;
	// 学生集合
	private Set<StudentDTO> students;
}
