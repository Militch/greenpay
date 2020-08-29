package com.esiran.greenadmin.system.entity.dot;

import com.esiran.greenadmin.common.util.PatternUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("UserInput")
public class UserUpdateDto {
    private Integer id;
    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 16, message = "请输入3-16位用户名")
    private String username;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Pattern(regexp = PatternUtil.REGEXP_EMAIL,message = "邮箱格式校验失败")
    private String email;


    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("用户角色")
    private String roleIds;

}
