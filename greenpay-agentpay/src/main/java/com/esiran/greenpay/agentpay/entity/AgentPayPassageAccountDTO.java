package com.esiran.greenpay.agentpay.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付通道账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class AgentPayPassageAccountDTO {
    private Integer id;
    private String payTypeCode;
    private String passageName;
    private Integer passageId;
    private String accountName;
    private String interfaceAttr;
    private Boolean status;
    private Integer weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
