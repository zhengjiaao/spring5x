package com.zja.entity.oneToOne;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户实体 用户对档案 基于外键的一对一
 */
@Getter
@Setter
public class UserOne {
	
	private int userId;
	private String userName;

	//档案
	private Resume resume;

}
