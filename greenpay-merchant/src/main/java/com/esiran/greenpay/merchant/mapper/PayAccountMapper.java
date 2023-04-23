package com.esiran.greenpay.merchant.mapper;

import com.esiran.greenpay.merchant.entity.PayAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商户支付账户 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface PayAccountMapper extends BaseMapper<PayAccount> {
    void updateAvailBalance(@Param("mchId") Integer mchId, @Param("amount") Integer amount);
    int updateFreezeBalance(@Param("mchId") Integer mchId, @Param("amount") Integer amount);
    int updateBalance(@Param("mchId") Integer mchId, @Param("availAmount") Integer availAmount,
                       @Param("freezeAmount") Integer freezeAmount);

}
