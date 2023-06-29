package com.esiran.greenpay.settle.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.settle.entity.SettleOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商户结算订单 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface SettleOrderMapper extends BaseMapper<SettleOrder> {

    List<SettleOrder> selectSettlesToday(@Param(Constants.WRAPPER) Wrapper<SettleOrder> wrapper);



}
