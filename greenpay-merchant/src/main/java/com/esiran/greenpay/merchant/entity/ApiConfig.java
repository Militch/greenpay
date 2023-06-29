package com.esiran.greenpay.merchant.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户密钥
 * </p>
 *
 * @author Militch
 * @since 2020-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("merchant_api_config")
public class ApiConfig extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    private Integer mchId;

    /**
     * 商户APIKEY
     */
    private String apiKey;

    /**
     * 商户API_SECURITY
     */
    private String apiSecurity;

    /**
     * 平台私钥
     */
    private String privateKey;
    /**
     * 平台公钥
     */
    private String pubKey;

    /**
     * 商户公钥
     */
    private String mchPubKey;


}
