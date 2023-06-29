package com.esiran.greenpay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.merchant.entity.PrepaidAccount;
import com.esiran.greenpay.merchant.entity.PrepaidAccountDTO;
import com.esiran.greenpay.merchant.entity.SettleAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.merchant.entity.SettleAccountDTO;

/**
 * <p>
 * 商户结算账户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-21
 */
public interface ISettleAccountService extends IService<SettleAccount> {
    SettleAccountDTO findByMerchantId(Integer mchId);

    SettleAccount findSettleAccountByMerchantId(Integer mchId);

    boolean removeByMerchantId(Integer merchantId);
}
