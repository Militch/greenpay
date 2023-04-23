package com.esiran.greenpay.system.entity.dot;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.esiran.greenpay.common.util.PatternUtil;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * @author han
 */
@Data
public class UserInputDto  {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 16, message = "用户名过短")
    private String username;


    /**
     * 用户密码
     */
//    @NotBlank(message = "用户密码不能为空")
    private String password;


    /**
     * 用户邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Pattern(regexp = PatternUtil.REGEXP_EMAIL,message = "邮箱格式校验失败")
    private String email;

    private String roleIds;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
