package com.esiran.greenpay.settle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.HomeDateVo;
import com.esiran.greenpay.merchant.entity.StatisticDTO;
import com.esiran.greenpay.pay.entity.ExtractQueryDTO;
import com.esiran.greenpay.settle.entity.SettleOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.settle.entity.SettleOrderDTO;
import com.esiran.greenpay.settle.entity.SettleOrderInputDTO;
import com.esiran.greenpay.settle.entity.SettleOrderQueryDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商户结算订单 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface ISettleOrderService extends IService<SettleOrder> {
    IPage<SettleOrderDTO> selectPage(IPage<SettleOrderDTO> page, SettleOrderDTO orderDTO);
    IPage<SettleOrderDTO> selectPageByAudit(IPage<SettleOrderDTO> page, SettleOrderQueryDto settleOrderQueryDto);
    IPage<SettleOrderDTO> selectPageByPayable(IPage<SettleOrderDTO> page ,SettleOrderQueryDto settleOrderQueryDto);
    IPage<SettleOrderDTO> findPageByMchId(IPage<SettleOrderDTO> Page, Integer mchId);
    IPage<SettleOrderDTO> findPageByQuery(IPage<SettleOrderDTO> Page, Integer mchId, ExtractQueryDTO queryDTO);
    SettleOrderDTO getByOrderNo(String orderNo);
    void updateOrderStatus(String orderNo, Integer status) throws PostResourceException;
    void postOrder(SettleOrderInputDTO inputDTO) throws PostResourceException, ResourceNotFoundException;

    List<SettleOrder> selectSettlesToday();

    public HashMap<String,Object> findHomeDate();

    StatisticDTO sevenDaycartogram();
}
