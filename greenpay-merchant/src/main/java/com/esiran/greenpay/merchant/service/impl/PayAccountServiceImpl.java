package com.esiran.greenpay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.merchant.entity.*;
import com.esiran.greenpay.merchant.mapper.PayAccountMapper;
import com.esiran.greenpay.merchant.service.IPayAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户支付账户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class PayAccountServiceImpl extends ServiceImpl<PayAccountMapper, PayAccount> implements IPayAccountService {
    private static final ModelMapper modelMapper = new ModelMapper();
    @Override
    public PayAccountDTO findByMerchantId(Integer mchId) {
        LambdaQueryWrapper<PayAccount> payAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        payAccountLambdaQueryWrapper.eq(PayAccount::getMerchantId,mchId);
        PayAccount payAccount = this.getOne(payAccountLambdaQueryWrapper);
        PayAccountDTO dto = modelMapper.map(payAccount,PayAccountDTO.class);
        dto.setAvailBalanceDisplay(String.format("%.2f",(dto.getAvailBalance()/100.00f)));
        dto.setFreezeBalanceDisplay(String.format("%.2f",(dto.getFreezeBalance()/100.00f)));
        return dto;
    }

    @Override
    public PayAccount findPayAccountByMerchantId(Integer mchId) {
        LambdaQueryWrapper<PayAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PayAccount::getMerchantId, mchId);
        PayAccount payAccount = this.getOne(lambdaQueryWrapper);
        return payAccount;
    }


    @Override
    public void updateAvailBalance(Integer mchId, Integer amount) {
        if (mchId == null || amount == null) return;
        this.baseMapper.updateAvailBalance(mchId,amount);
    }

    @Override
    public int updateFreezeBalance(Integer mchId, Integer amount) {
        if (mchId == null || amount == null) return 0;
       return this.baseMapper.updateFreezeBalance(mchId,amount);
    }

    @Override
    public int updateBalance(Integer mchId, Integer availAmount, Integer freezeAmount) {
        if (mchId == null || availAmount == null || freezeAmount== null) return 0;
        return this.baseMapper.updateBalance(mchId,availAmount,freezeAmount);
    }

    @Override
    public boolean removeByMerchantId(Integer merchantId) {
        LambdaQueryWrapper<PayAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PayAccount::getMerchantId, merchantId);
        remove(lambdaQueryWrapper);
        return true;
    }
}
