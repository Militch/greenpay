package com.esiran.greenpay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.MerchantProductInputDTO;
import com.esiran.greenpay.merchant.entity.MerchantProductPassage;
import com.esiran.greenpay.merchant.mapper.MerchantProductPassageMapper;
import com.esiran.greenpay.merchant.service.IMerchantProductPassageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商户产品通道 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class MerchantProductPassageServiceImpl extends ServiceImpl<MerchantProductPassageMapper, MerchantProductPassage> implements IMerchantProductPassageService {

    @Override
    public void removeByProductId(Integer mchId, Integer productId) {
        LambdaUpdateWrapper<MerchantProductPassage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MerchantProductPassage::getMchId,mchId);
        updateWrapper.eq(MerchantProductPassage::getProductId,productId);
        remove(updateWrapper);
    }

    @Override
    public List<MerchantProductPassage> listAvailable(Integer mchId, Integer productId) {
        LambdaQueryWrapper<MerchantProductPassage> mppQueryWrapper = new LambdaQueryWrapper<>();
        mppQueryWrapper.eq(MerchantProductPassage::getMchId,mchId)
                .eq(MerchantProductPassage::getProductId,productId)
                .gt(MerchantProductPassage::getWidget,0)
                .orderByDesc(MerchantProductPassage::getWidget);
        return list(mppQueryWrapper);
    }

    @Override
    public List<MerchantProductPassage> listByProductId(Integer mchId, Integer productId) {
        LambdaQueryWrapper<MerchantProductPassage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantProductPassage::getMchId,mchId);
        queryWrapper.eq(MerchantProductPassage::getProductId,productId);
        return list(queryWrapper);
    }

    @Override
    public boolean updateById(Integer id, MerchantProductInputDTO productInputDTO) throws PostResourceException, ResourceNotFoundException {
        return false;
    }

    @Override
    public boolean removeByMerchantId(Integer merchantId) {
        LambdaQueryWrapper<MerchantProductPassage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MerchantProductPassage::getMchId, merchantId);
        remove(lambdaQueryWrapper);
        return true;
    }
}
