package com.zja.entity.oneToOnePk;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

/**
 * 档案实体  resume:user 基于主键的一对一
 */
@Getter
@Setter
public class ResumePk {
	
	private int resId;
	private String resName;

	//用户
	@JsonBackReference
	private UserPk user;

}
