package com.esiran.greenpay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.merchant.entity.MerchantAgentPayPassage;
import com.esiran.greenpay.merchant.entity.MerchantAgentPayPassageDTO;
import com.esiran.greenpay.merchant.entity.MerchantAgentPayPassageInputDTO;
import com.esiran.greenpay.merchant.entity.MerchantProduct;
import com.esiran.greenpay.merchant.mapper.MerchantAgentPayPassageMapper;
import com.esiran.greenpay.merchant.service.IMerchantAgentPayPassageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商户代付通道 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Service
public class MerchantAgentPayPassageServiceImpl extends ServiceImpl<MerchantAgentPayPassageMapper, MerchantAgentPayPassage> implements IMerchantAgentPayPassageService {
    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public MerchantAgentPayPassage getOneByMchId(Integer mchId, Integer passageId) {
        LambdaQueryWrapper<MerchantAgentPayPassage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantAgentPayPassage::getMerchantId,mchId)
                .eq(MerchantAgentPayPassage::getPassageId,passageId);
        return getOne(queryWrapper);
    }

    @Override
    public List<MerchantAgentPayPassage> listAvailableByMchId(Integer mchId) {
        LambdaQueryWrapper<MerchantAgentPayPassage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantAgentPayPassage::getMerchantId, mchId)
                .eq(MerchantAgentPayPassage::getStatus, 1)
                .gt(MerchantAgentPayPassage::getWeight, 0);
        return this.list(queryWrapper);
    }

    @Override
    public MerchantAgentPayPassageDTO getOneDTOByMchId(Integer mchId, Integer passageId) {
        MerchantAgentPayPassage mapp = getOneByMchId(mchId,passageId);
        if (mapp == null) return null;
        MerchantAgentPayPassageDTO dto = modelMapper.map(mapp,MerchantAgentPayPassageDTO.class);
        dto.setFeeAmountDisplay(NumberUtil.amountFen2Yuan(mapp.getFeeAmount()));
        dto.setFeeRateDisplay(NumberUtil.twoDecimals(mapp.getFeeRate()));
        return dto;
    }

    @Override
    @Transactional
    public void updateByInput(MerchantAgentPayPassageInputDTO inputDTO) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MerchantAgentPayPassage mapp = modelMapper.map(inputDTO,MerchantAgentPayPassage.class);
        LambdaUpdateWrapper<MerchantAgentPayPassage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MerchantAgentPayPassage::getMerchantId,inputDTO.getMerchantId())
                .eq(MerchantAgentPayPassage::getPassageId,mapp.getPassageId());
        remove(updateWrapper);
        mapp.setCreatedAt(LocalDateTime.now());
        mapp.setUpdatedAt(LocalDateTime.now());
        save(mapp);
    }

    @Override
    public boolean removeByMerchantId(Integer merchantId) {
        LambdaQueryWrapper<MerchantAgentPayPassage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MerchantAgentPayPassage::getMerchantId, merchantId);
        remove(lambdaQueryWrapper);
        return true;
    }
}
