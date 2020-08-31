package com.esiran.greenadmin.system.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleInputDto {
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;
}
