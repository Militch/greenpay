package com.esiran.greenpay.settle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.settle.entity.SettleConfig;
import com.esiran.greenpay.settle.entity.SettleConfigDTO;
import com.esiran.greenpay.settle.entity.SettleConfigInputDTO;
import com.esiran.greenpay.settle.mapper.SettleConfigMapper;
import com.esiran.greenpay.settle.service.ISettleConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 结算设置 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class SettleConfigServiceImpl extends ServiceImpl<SettleConfigMapper, SettleConfig> implements ISettleConfigService {
    private static final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void update(SettleConfigInputDTO inputDTO) {
        LambdaQueryWrapper<SettleConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("LIMIT 1");
        SettleConfig target = this.getOne(queryWrapper);
        if (target == null) return;
        SettleConfig config = modelMapper.map(inputDTO,SettleConfig.class);
        config.setId(target.getId());
        config.setAmountLimitMax(NumberUtil.amountYuan2fen(
                inputDTO.getAmountLimitMaxVal()));
        config.setAmountLimitMin(NumberUtil.amountYuan2fen(
                inputDTO.getAmountLimitMinVal()));
        config.setDayAmountLimitMax(NumberUtil.amountYuan2fen(
                inputDTO.getDayAmountLimitMaxVal()));
        config.setSettleFee(NumberUtil.amountYuan2fen(
                inputDTO.getSettleFeeVal()));
        updateById(config);
    }

    @Override
    public SettleConfigDTO get() {
        LambdaQueryWrapper<SettleConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("LIMIT 1");
        SettleConfig target = this.getOne(queryWrapper);
        if (target == null) return null;
        SettleConfigDTO config = modelMapper.map(target,SettleConfigDTO.class);
        config.setAmountLimitMinDisplay(NumberUtil.amountFen2Yuan(
                config.getAmountLimitMin()));
        config.setAmountLimitMaxDisplay(NumberUtil.amountFen2Yuan(
                config.getAmountLimitMax()));
        config.setDayAmountLimitMaxDisplay(NumberUtil.amountFen2Yuan(
                config.getDayAmountLimitMax()));
        config.setSettleRateDisplay(NumberUtil.twoDecimals(
                config.getSettleRate()));
        config.setSettleFeeDisplay(NumberUtil.amountFen2Yuan(
                config.getSettleFee()));
        return config;
    }
}
