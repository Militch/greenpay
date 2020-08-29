package com.esiran.greenadmin.system.entity.dot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
@ApiModel("RoleDto")
public class UserRoleInputDto {
    private Integer id;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 角色编码
     */
    @ApiModelProperty("角色编码")
    private String roleCode;

    /**
     * 角色
     */
    @ApiModelProperty("角色权限")
    private String permIds;

    private String permissionIds;
}
