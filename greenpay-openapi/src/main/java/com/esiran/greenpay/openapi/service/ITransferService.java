package com.esiran.greenpay.openapi.service;

import com.esiran.greenpay.agentpay.entity.AgentPayBatchInputDTO;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.openapi.entity.BatchInputDTO;
import com.esiran.greenpay.openapi.entity.Transfer;
import com.esiran.greenpay.openapi.entity.TransferInputDTO;

public interface ITransferService {
    Transfer createOneByInput(Integer mchId, TransferInputDTO inputDTO) throws APIException;
    void batch(BatchInputDTO batchInputDTO, Integer mchId ) throws APIException;
}
