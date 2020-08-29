package com.esiran.greenadmin.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import com.esiran.greenadmin.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统用户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("system_user")
public class User extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户密码
     */
    private String password;
    /**
     * 两步验证安全码
     */
    private String totpSecretKey;
    private Boolean totpVerified;
    /**
     * 用户邮箱
     */
    private String email;

}
