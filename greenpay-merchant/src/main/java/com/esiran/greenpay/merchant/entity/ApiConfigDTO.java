package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
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
public class ApiConfigDTO {

    /**
     * 商户APIKEY
     */
    private String apiKey;

    /**
     * 商户API_SECURITY
     */
    private String apiSecurity;

    /**
     * 平台公钥
     */
    private String pubKey;
    private String privateKey;

    private String pubKeyVal;
    private String privateKeyVal;

    /**
     * 商户公钥
     */
    private String mchPubKey;
    private String mchPubKeyVal;

}
