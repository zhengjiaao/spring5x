package com.zja.entity.oneToOnePk;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户实体  UserPk:ResumePk 基于主键的一对一
 */
@Getter
@Setter
public class UserPk {
	
	private int userId;
	private String userName;

	//档案
	@JsonBackReference
	private ResumePk resume;

}
