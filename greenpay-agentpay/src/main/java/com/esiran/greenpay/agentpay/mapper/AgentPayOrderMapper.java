package com.esiran.greenpay.agentpay.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import org.apache.ibatis.annotations.Param;
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
    List<AgentPayOrderDTO> findIntradayOrdersByMchId(Integer mchId);

    //查询昨天成功订单总数
    @Select("SELECT * FROM agentpay_order WHERE DATEDIFF(now(),created_at) = 1 AND mch_id = #{mchId} ")
    List<AgentPayOrderDTO> findYesterdayOrdersByMchId(Integer mchId);

    @Select("SELECT * FROM agentpay_order WHERE DATEDIFF(now(),created_at) = 0 ")
    List<AgentPayOrderDTO> findIntradayOrders();

    @Select("SELECT * FROM agentpay_order WHERE DATEDIFF(now(),created_at) = 1 ")
    List<AgentPayOrderDTO> findYesterdayOrders();

    List<AgentPayOrder> agentPayOrderList(@Param(Constants.WRAPPER) Wrapper<AgentPayOrder> wrapper,Long page,Long size);



    //24小时数据
    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,     " +
            "                               SUM(amount) as amount,     " +
            "                               COUNT(1) AS count,     " +
            "                               SUM((IF(status = 3, amount, 0))) AS successAmount,     " +
            "                               COUNT(IF(status = 3, 1, 0)) AS successCount     " +
            "                        FROM agentpay_order     " +
            "                        WHERE  date_format(created_at, '%Y%m%d') = DATE_FORMAT(NOW(),'%Y%m%d')     " +
            "                        GROUP BY name")
    List<CartogramDTO> hourAllData();

    //7日数据
    @Select("SELECT DATE_FORMAT(created_at,'%m-%d') AS name,    " +
            "                          COUNT(*) AS count,   " +
            "                          SUM((IF (status =3 ,1,0))) AS successCount,   " +
            "                          SUM(amount) as amount,   " +
            "                          SUM((IF(status = 3,amount,0))) as successAmount   " +
            "                        FROM agentpay_order   " +
            "                        WHERE  DATE_SUB(CURDATE(),INTERVAL 7 day) <=date(created_at)   " +
            "                        GROUP BY name")
    List<CartogramDTO> sevenDayAllData();


    //上周的数据
    @Select("select  WEEKDAY(created_at)+1  AS name,   " +
            "COUNT(*) AS count,    " +
            "SUM((IF (status = 2 OR status = 3 ,1,0))) AS successCount,     " +
            "SUM(amount) AS amount,     " +
            "SUM((if(status = 2 OR status = 3 ,amount,0))) as successAmount     " +
            "from agentpay_order WHERE YEARWEEK(date_format(created_at,'%Y-%m-%d')) = YEARWEEK(now()) -1 " +
            "GROUP BY name;")
    List<CartogramDTO> upWeekAllData();


    //一周的数据
    @Select("select  WEEKDAY(created_at)+1  AS name,   " +
            "COUNT(*) AS count,    " +
            "SUM((IF (status = 2 OR status = 3 ,1,0))) AS successCount,     " +
            "SUM(amount) AS amount,     " +
            "SUM((if(status = 2 OR status = 3 ,amount,0))) as successAmount     " +
            "from agentpay_order WHERE YEARWEEK(date_format(created_at,'%Y-%m-%d')) = YEARWEEK(now())  " +
            "GROUP BY name;")
    List<CartogramDTO> sevenDay4CountAndAmount();

    //本月的数据
    @Select("select  DATE_FORMAT(created_at,'%e') AS name,   " +
            "COUNT(*) AS count,   " +
            "SUM((IF (status = 3 ,1,0 ))) AS successCount,   " +
            "SUM(amount) AS amount,   " +
            "SUM((if(status = 3 ,amount,0))) as successAmount   " +
            "from agentpay_order where DATE_FORMAT(created_at,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')    " +
            "GROUP BY name;")
    List<CartogramDTO> currentMonth4CountAndAmount();


    //支付排行
    @Select("SELECT agentpay_passage_name AS payname,COUNT(*) AS count, SUM(amount) as amount FROM agentpay_order WHERE DATE_SUB(CURDATE(),INTERVAL 7 day)<= DATE(created_at) GROUP BY payname ORDER BY amount DESC")
    List<CartogramPayDTO> payRanking();

    //转化率
    @Select("SELECT status,COUNT(*) as count FROM agentpay_order WHERE DATEDIFF(now(),created_at) = 0 group by status")
    List<CartogramPayStatusVo> payCRV();

}
