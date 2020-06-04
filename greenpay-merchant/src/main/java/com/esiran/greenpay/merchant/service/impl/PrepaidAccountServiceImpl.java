package com.esiran.greenpay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.merchant.entity.MerchantProductPassage;
import com.esiran.greenpay.merchant.entity.PayAccount;
import com.esiran.greenpay.merchant.entity.PayAccountDTO;
import com.esiran.greenpay.merchant.entity.PrepaidAccount;
import com.esiran.greenpay.merchant.entity.PrepaidAccountDTO;
import com.esiran.greenpay.merchant.mapper.PrepaidAccountMapper;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商户预充值账户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class PrepaidAccountServiceImpl extends ServiceImpl<PrepaidAccountMapper, PrepaidAccount> implements IPrepaidAccountService {
    private static final ModelMapper modelMapper = new ModelMapper();
    @Override
    public PrepaidAccountDTO findByMerchantId(Integer mchId) {
        PrepaidAccount prepaidAccount = this.getByMerchantId(mchId);
        if (prepaidAccount == null) return null;
        PrepaidAccountDTO dto = modelMapper.map(prepaidAccount,PrepaidAccountDTO.class);
        dto.setAvailBalanceDisplay(String.format("%.2f",(dto.getAvailBalance()/100.00f)));
        dto.setFreezeBalanceDisplay(String.format("%.2f",(dto.getFreezeBalance()/100.00f)));
        return dto;
    }

    @Override
    public PrepaidAccount getByMerchantId(Integer mchId) {
        LambdaQueryWrapper<PrepaidAccount> payAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        payAccountLambdaQueryWrapper.eq(PrepaidAccount::getMerchantId,mchId);
        return this.getOne(payAccountLambdaQueryWrapper);
    }

    @Override
    public int updateBalance(Integer mchId, Integer availAmount, Integer freezeAmount) {
        if (mchId == null || availAmount == null || freezeAmount== null) return 0;
        return this.baseMapper.updateBalance(mchId,availAmount,freezeAmount);
    }

    @Override
    public Integer selectAvailBalance(Integer mchId, Integer amount) {
        if (mchId == null || amount == null) return null;
        return this.baseMapper.selectAvailBalance(mchId, amount);
    }

    @Override
    public boolean removeByMerchantId(Integer merchantId) {
        LambdaQueryWrapper<PrepaidAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PrepaidAccount::getMerchantId, merchantId);
        remove(lambdaQueryWrapper);
        return true;
    }


}
