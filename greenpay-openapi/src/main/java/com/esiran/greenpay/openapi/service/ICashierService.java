package com.esiran.greenpay.openapi.service;

import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.openapi.entity.CashierInputDTO;
import com.esiran.greenpay.pay.entity.PayOrder;

public interface ICashierService {
    PayOrder createCashierByInput(String productCode, CashierInputDTO inputDTO, Merchant merchant) throws Exception;
}
