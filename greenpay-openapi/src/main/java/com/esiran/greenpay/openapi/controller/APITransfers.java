package com.esiran.greenpay.openapi.controller;

import com.esiran.greenpay.agentpay.entity.AgentPayBatchInputDTO;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.openapi.entity.Transfer;
import com.esiran.greenpay.openapi.entity.TransferInputDTO;
import com.esiran.greenpay.openapi.security.OpenAPISecurityUtils;
import com.esiran.greenpay.openapi.service.ITransferService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transfers")
public class APITransfers {
    private final ITransferService transferService;

    public APITransfers(ITransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public Transfer create(@Valid TransferInputDTO inputDTO) throws APIException {
        Merchant m = OpenAPISecurityUtils.getSubject();
        Transfer t = transferService.createOneByInput(m.getId(),inputDTO);
        return t;
    }
    @PostMapping("/batch")
    public void batch(@Valid AgentPayBatchInputDTO agentPayBatchInputDTO){

    }
}
