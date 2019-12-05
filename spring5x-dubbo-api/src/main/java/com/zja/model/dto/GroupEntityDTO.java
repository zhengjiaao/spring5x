package com.zja.model.dto;

import com.zja.model.entity.UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhengJa
 * @description 用户组
 * @data 2019/11/18
 */
@Data
@ApiModel("用户组实体类")
public class GroupEntityDTO implements Serializable {
    @ApiModelProperty("组id")
    private Integer groupId;
    @ApiModelProperty("用户组名称")
    private String groupName;

    @ApiModelProperty("组下的所有用户信息")
    private List<UserEntity> userEntityList;
}
