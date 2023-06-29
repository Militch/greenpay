package com.esiran.greenpay.pay.service;

import com.esiran.greenpay.common.exception.PostResourceException;

public interface IOrderNotifyService {
    boolean notifyByOrderNo(String orderNo, String credential) throws PostResourceException;
    boolean notifyByOrderNo(String orderNo, String credential,String sigType) throws PostResourceException;
}
