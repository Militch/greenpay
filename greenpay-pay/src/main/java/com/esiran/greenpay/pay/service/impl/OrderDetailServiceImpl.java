package com.esiran.greenpay.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;
import com.esiran.greenpay.pay.mapper.OrderDetailMapper;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 订单详情 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {

    private final static ModelMapper modelMapper = new ModelMapper();
    private final static Gson gson = new GsonBuilder().create();
    @Override
    public OrderDetailDTO getByOrderNo(String orderNo) {
        OrderDetail detail = this.getOneByOrderNo(orderNo);
        if (detail == null) return null;
        return modelMapper.map(detail,OrderDetailDTO.class);
    }

    @Override
    public OrderDetail getOneByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderNo, orderNo);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public void updatePayCredentialByOrderNo(String orderNo, Map<String, Object> credential) {
        if (credential == null) return;
        String credentialJson = gson.toJson(credential,new TypeToken<Map<String,Object>>(){}.getType());
        LambdaUpdateWrapper<OrderDetail> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(OrderDetail::getPayCredential,credentialJson)
                .set(OrderDetail::getUpdatedAt, LocalDateTime.now())
                .eq(OrderDetail::getOrderNo,orderNo);
        this.update(lambdaUpdateWrapper);
    }
}
