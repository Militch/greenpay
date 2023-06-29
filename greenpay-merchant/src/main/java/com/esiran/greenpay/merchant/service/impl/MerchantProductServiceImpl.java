package com.esiran.greenpay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.*;
import com.esiran.greenpay.merchant.mapper.MerchantProductMapper;
import com.esiran.greenpay.merchant.service.IMerchantProductPassageService;
import com.esiran.greenpay.merchant.service.IMerchantProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.service.IPassageAccountService;
import com.esiran.greenpay.pay.service.IPassageService;
import com.esiran.greenpay.pay.service.IProductService;
import com.esiran.greenpay.pay.service.ITypeService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商户产品 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class MerchantProductServiceImpl extends ServiceImpl<MerchantProductMapper, MerchantProduct> implements IMerchantProductService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final Gson gson = new Gson();
    private final IProductService productService;
    private final ITypeService typeService;
    private final IPassageService passageService;
    private final IPassageAccountService passageAccountService;
    private final IMerchantProductPassageService merchantProductPassageService;

    public MerchantProductServiceImpl(
            IProductService productService,
            ITypeService typeService,
            IPassageService passageService,
            IPassageAccountService passageAccountService, IMerchantProductPassageService merchantProductPassageService) {
        this.productService = productService;
        this.typeService = typeService;
        this.passageService = passageService;
        this.passageAccountService = passageAccountService;
        this.merchantProductPassageService = merchantProductPassageService;
    }

    @Override
    public MerchantProductDTO getByProductId(Integer mchId, Integer productId) {
        LambdaQueryWrapper<MerchantProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantProduct::getMerchantId, mchId)
                .eq(MerchantProduct::getProductId, productId);
        MerchantProduct mp = this.getOne(queryWrapper);
        if (mp == null) return null;
        return modelMapper.map(mp,MerchantProductDTO.class);
    }

    @Override
    public MerchantProductDTO getAvailableByProductId(Integer mchId, Integer productId) {
        LambdaQueryWrapper<MerchantProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantProduct::getMerchantId, mchId)
                .eq(MerchantProduct::getStatus, 1)
                .eq(MerchantProduct::getProductId, productId);
        MerchantProduct mp = this.getOne(queryWrapper);
        if (mp == null) return null;
        return modelMapper.map(mp,MerchantProductDTO.class);
    }

    @Override
    public boolean updateById(MerchantProductInputDTO merchantProductInputDTO) throws ResourceNotFoundException, PostResourceException {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Product src = productService.getById(merchantProductInputDTO.getProductId());
        if (src == null) throw new ResourceNotFoundException("支付产品不存在");
        MerchantProduct target = modelMapper.map(merchantProductInputDTO,MerchantProduct.class);
        if (merchantProductInputDTO.getDefaultPassageId() != null){
            Passage passage = passageService.getById(merchantProductInputDTO.getDefaultPassageId());
            if (passage == null)
                throw new PostResourceException("支付通道不存在");
            if (merchantProductInputDTO.getDefaultPassageAccId() == null)
                throw new PostResourceException("支付通道子账户不能为空");
            PassageAccount passageAcc = passageAccountService.getById(merchantProductInputDTO.getDefaultPassageAccId());
            if (passageAcc == null)
                throw new PostResourceException("支付通道子账户不存在");
            if (!passage.getId().equals(passageAcc.getPassageId())){
                throw new PostResourceException("支付通道与子账户不匹配");
            }
        }
        String loopPassages = merchantProductInputDTO.getLoopPassages();
        if (!StringUtils.isEmpty(loopPassages)){
            List<MerchantProductPassageInputDTO> passageInputDTOS = gson.fromJson(loopPassages,
                    new TypeToken<List<MerchantProductPassageInputDTO>>(){}.getType());
            List<MerchantProductPassageInputDTO> passagesDTOs = passageInputDTOS.stream()
                    .filter(MerchantProductPassageInputDTO::getUsage)
                    .collect(Collectors.toList());
            List<MerchantProductPassage> passages = passagesDTOs.stream()
                    .map(item->modelMapper.map(item,MerchantProductPassage.class))
                    .collect(Collectors.toList());
            merchantProductPassageService.removeByProductId(target.getMerchantId(), target.getProductId());
            passages.forEach(merchantProductPassageService::save);
        }
        removeByProductId(target.getMerchantId(),target.getProductId());
        return this.save(target);
    }

    @Override
    public void removeByProductId(Integer merchantId, Integer productId) {
        LambdaUpdateWrapper<MerchantProduct> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MerchantProduct::getMerchantId,merchantId)
                .eq(MerchantProduct::getProductId,productId);
        remove(updateWrapper);
    }

    @Override
    public void removeByMerchantId(Integer mchId) {
        LambdaQueryWrapper<MerchantProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MerchantProduct::getMerchantId, mchId);

        remove(lambdaQueryWrapper);
    }
}
