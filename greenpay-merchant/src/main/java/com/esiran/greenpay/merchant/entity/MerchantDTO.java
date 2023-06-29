package com.esiran.greenpay.merchant.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
public class MerchantDTO {
    private Integer id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Boolean status;
    private PayAccountDTO payAccount;
    private PrepaidAccountDTO prepaidAccount;
    private SettleAccountDTO settleAccountDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
