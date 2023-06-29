package com.esiran.greenpay.merchant.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Merchant extends BaseMapperEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 商户名称
     */
    private String name;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 联系手机
     */
    private String phone;
    /**
     * 商户状态（0：禁用，1：启用）
     */
    private Boolean status;
    /**
     * 商户登录密码
     */
    private String password;

}
