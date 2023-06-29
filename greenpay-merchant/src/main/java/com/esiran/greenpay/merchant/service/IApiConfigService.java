package com.esiran.greenpay.merchant.service;

import com.esiran.greenpay.merchant.entity.ApiConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.merchant.entity.ApiConfigDTO;

/**
 * <p>
 * 商户密钥 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-15
 */
public interface IApiConfigService extends IService<ApiConfig> {
    ApiConfigDTO findByMerchantId(Integer mchId);
    ApiConfig getOneByMerchantId(Integer mchId);

    boolean removeByMerchantId(Integer merchantId);
}
