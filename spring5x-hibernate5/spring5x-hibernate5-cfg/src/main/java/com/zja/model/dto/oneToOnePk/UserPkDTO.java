package com.zja.model.dto.oneToOnePk;

import lombok.Data;

/**
 * 用户实体  UserPk:ResumePk 基于主键的一对一
 */
@Data
public class UserPkDTO {
	
	private int userId;
	private String userName;

	//档案
	private ResumePkDTO resume;

}
