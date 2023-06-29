package com.esiran.greenpay.openapi.service.impl;

import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.openapi.entity.CashierInputDTO;
import com.esiran.greenpay.openapi.entity.InvoiceInputDTO;
import com.esiran.greenpay.openapi.service.ICashierService;
import com.esiran.greenpay.openapi.service.IInvoiceService;
import com.esiran.greenpay.openapi.service.IPayloadService;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.entity.PayOrder;
import com.esiran.greenpay.pay.plugin.PayOrderFlow;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CashierService implements ICashierService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final Gson gson = new GsonBuilder().create();
    private final IInvoiceService invoiceService;
    public CashierService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrder createCashierByInput(String productCode, CashierInputDTO inputDTO, Merchant merchant) throws Exception {
        InvoiceInputDTO invoiceInputDTO = modelMapper.map(inputDTO, InvoiceInputDTO.class);
        invoiceInputDTO.setChannel(productCode);
        return invoiceService.createPretreatmentInvoice(invoiceInputDTO,merchant);
    }
}
