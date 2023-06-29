package com.esiran.greenpay.settle.service;

import com.esiran.greenpay.settle.entity.SettleConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.settle.entity.SettleConfigDTO;
import com.esiran.greenpay.settle.entity.SettleConfigInputDTO;

/**
 * <p>
 * 结算设置 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface ISettleConfigService extends IService<SettleConfig> {
    void update(SettleConfigInputDTO inputDTO);
    SettleConfigDTO get();
}
