package com.esiran.greenpay.openapi.service;

import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.openapi.entity.Invoice;
import com.esiran.greenpay.openapi.entity.InvoiceInputDTO;
import com.esiran.greenpay.openapi.entity.InvoiceQueryInputDTO;
import com.esiran.greenpay.pay.entity.PayOrder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IInvoiceService {
    Invoice createInvoiceByInput(InvoiceInputDTO invoiceInputDTO, Merchant merchant) throws Exception;
    PayOrder createPretreatmentInvoice(InvoiceInputDTO invoiceInputDTO, Merchant merchant) throws APIException;
    String handleInvoiceCallback(HttpServletRequest request, HttpServletResponse response, String orderNo);
    Invoice getOneByQuery(InvoiceQueryInputDTO queryInputDTO) throws ResourceNotFoundException;
}
