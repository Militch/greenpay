package com.esiran.greenpay.merchant.service;

import com.esiran.greenpay.merchant.entity.PayAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.merchant.entity.PayAccountDTO;

/**
 * <p>
 * 商户支付账户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IPayAccountService extends IService<PayAccount> {
    PayAccountDTO findByMerchantId(Integer mchId);
    PayAccount findPayAccountByMerchantId(Integer mchId);
    void updateAvailBalance(Integer mchId, Integer amount);
    int updateFreezeBalance(Integer mchId, Integer amount);
    int updateBalance(Integer mchId, Integer availAmount, Integer freezeAmount);

    boolean removeByMerchantId(Integer merchantId);
}
