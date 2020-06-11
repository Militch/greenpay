package com.esiran.greenpay.openapi.service;

import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.openapi.entity.*;

public interface ITransferService {
    Transfer createOneByInput(Integer mchId, TransferInputDTO inputDTO) throws APIException;
    BatchRes batch(BatchInputDTO batchInputDTO, Integer mchId ) throws APIException;
    String queryAmount (Integer mchId) throws APIException;
    AgentPayRes queryAgentPay(String outOrderNo, String orderNo) throws APIException;
    BatchRes queryBatch(String outBatchNo, String batchNo) throws APIException;

    String prepaid(Integer id);
}
