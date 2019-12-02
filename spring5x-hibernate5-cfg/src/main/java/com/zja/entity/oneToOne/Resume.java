package com.zja.entity.oneToOne;


import lombok.Getter;
import lombok.Setter;

/**
 * 档案实体 resume:user 基于外键的一对一 [多对一 (unique=true)]
 */
@Getter
@Setter
public class Resume {
	
	private int resId;
	private String resName;

	//用户
	private UserOne user;

}
