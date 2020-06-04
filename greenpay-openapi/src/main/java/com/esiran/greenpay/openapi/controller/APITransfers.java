package com.esiran.greenpay.openapi.controller;

import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.openapi.entity.*;
import com.esiran.greenpay.openapi.security.OpenAPISecurityUtils;
import com.esiran.greenpay.openapi.service.ITransferService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transfers")
public class APITransfers {

    private final ITransferService transferService;

    public APITransfers(ITransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("")
    public String queryAmount() throws APIException {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        return transferService.queryAmount(merchant.getId());
    }
    @GetMapping("/singles")
    public AgentPayRes queryagentpay(@RequestParam("outOrderNo") String outOrderNo,
                                     @RequestParam("orderNo") String orderNo) throws APIException {
        return transferService.queryAgentPay(outOrderNo,orderNo);
    }
    @PostMapping("/singles")
    public Transfer create(@Valid TransferInputDTO inputDTO) throws APIException {
        Merchant m = OpenAPISecurityUtils.getSubject();
        Transfer t = transferService.createOneByInput(m.getId(),inputDTO);
        return t;
    }
    @GetMapping("/batches")
    public BatchRes queryBacth(@RequestParam("batchNo") String outBatchNo,
                               @RequestParam("batchNo") String batchNo) throws APIException {
        return transferService.queryBatch(outBatchNo,batchNo);
    }
    @PostMapping("/batches")
    public BatchRes batch(@Valid BatchInputDTO batchInputDTO) throws APIException {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        return transferService.batch(batchInputDTO,merchant.getId());
    }
}
