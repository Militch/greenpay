package com.esiran.greenpay.merchant.service;

import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.MerchantProductInputDTO;
import com.esiran.greenpay.merchant.entity.MerchantProductPassage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商户产品通道 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface IMerchantProductPassageService extends IService<MerchantProductPassage> {
    void removeByProductId(Integer mchId,Integer productId);
    List<MerchantProductPassage> listAvailable(Integer mchId,Integer productId);
    List<MerchantProductPassage> listByProductId(Integer mchId,Integer productId);

    boolean updateById(Integer id, MerchantProductInputDTO productInputDTO) throws PostResourceException, ResourceNotFoundException;

    boolean removeByMerchantId(Integer merchantId);
}
