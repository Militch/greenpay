package com.esiran.greenpay.system.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author han
 * @Package com.esiran.greenpay.system.entity
 * @date 2020/4/29 16:43
 */
@Data
public class UserInputVo {


    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;


    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

}
