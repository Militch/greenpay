package com.esiran.greenpay.agentpay.mapper;

import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 代付订单表 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface AgentPayOrderMapper extends BaseMapper<AgentPayOrder> {
    //查询当天订单总数
    @Select("SELECT * FROM agentpay_order WHERE DATEDIFF(now(),created_at) = 0  AND mch_id = #{mchId}")
    List<AgentPayOrderDTO> findIntradayOrders(Integer mchId);

    //查询昨天成功订单总数
    @Select("SELECT * FROM agentpay_order WHERE DATEDIFF(now(),created_at) = 1 AND mch_id = #{mchId} AND status = 3")
    List<AgentPayOrderDTO>  findYesterdayOrders(Integer mchId);

}
