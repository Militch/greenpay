package com.esiran.greenpay.pay.service;

import com.esiran.greenpay.pay.entity.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;

import java.util.Map;

/**
 * <p>
 * 订单详情 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
public interface IOrderDetailService extends IService<OrderDetail> {
    OrderDetailDTO getByOrderNo(String orderNo);
    OrderDetail getOneByOrderNo(String orderNo);
    void updatePayCredentialByOrderNo(String orderNo, Map<String,Object> credential);
}
