package com.zja.model.dto.oneToOnePk;


import lombok.Data;

/**
 * 档案实体  resume:user 基于主键的一对一
 */
@Data
public class ResumePkDTO {
	
	private int resId;
	private String resName;

	//用户
	private UserPkDTO user;

}
