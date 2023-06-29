package com.esiran.greenpay.merchant.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantDetailDTO extends BaseMapperEntity {
    private String username;
    private String name;
    private String email;
    private String phone;
    private Boolean status;
    private ApiConfigDTO apiConfig;
    private PayAccountDTO payAccount;
    private PrepaidAccountDTO prepaidAccount;
    private SettleAccountDTO settleAccount;
}
