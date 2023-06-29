package com.esiran.greenpay.merchant.service;

import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.MerchantProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.merchant.entity.MerchantProductDTO;
import com.esiran.greenpay.merchant.entity.MerchantProductInputDTO;

/**
 * <p>
 * 商户产品 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IMerchantProductService extends IService<MerchantProduct> {
    MerchantProductDTO getByProductId(Integer mchId, Integer productId);
    MerchantProductDTO getAvailableByProductId(Integer mchId, Integer productId);
    boolean updateById(MerchantProductInputDTO merchantProductInputDTO) throws ResourceNotFoundException, PostResourceException;
    void removeByProductId(Integer merchantId, Integer productId);

    void removeByMerchantId(Integer mchId);
}
