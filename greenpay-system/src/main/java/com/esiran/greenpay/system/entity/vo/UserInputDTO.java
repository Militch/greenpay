package com.esiran.greenpay.system.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 系统用户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class UserInputDTO {
    @NotBlank(message = "用户名称不能为空")
    private String username;
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
