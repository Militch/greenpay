package com.esiran.greenpay.pay.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import com.esiran.greenpay.pay.entity.HourData;
import com.esiran.greenpay.pay.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 支付订单 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
public interface OrderMapper extends BaseMapper<Order> {
    @Select("select * from pay_order where to_days(created_at)=to_days(now()) AND mch_id = #{mchId}")
    List<Order> getByDay(Integer mchId);


    List<Order> findIntradayOrder(@Param(Constants.WRAPPER) Wrapper<Order> wrapper);


    //查询当天成功订单总数
    @Select("SELECT count(order_no) FROM pay_order WHERE DATEDIFF(now(),created_at) = 0 AND (`status` = 3 OR `status` = 2)")
    Integer findIntradayOrderSucc();

    //查询昨天成功订单总数
    @Select("SELECT count(order_no) FROM pay_order WHERE DATEDIFF(now(),created_at) = 1 AND (`status` = 3 OR `status` = 2)")
    Integer findYesterdayOrderSucc();


    //查询昨天0点到昨天当前时间总订单数
    @Select("SELECT count(order_no) FROM pay_order WHERE created_at BETWEEN DATE_FORMAT(DATE_SUB(NOW(),interval 1 day),'%Y-%m-%d 00:00:00')" +
            "AND DATE_FORMAT(DATE_SUB(NOW(),interval 1 day),'%Y-%m-%d %T')")
    Integer yestdayRealorderData();


    //查询今日0点到当前时间总订单数
    @Select("SELECT count(order_no) FROM pay_order WHERE created_at BETWEEN DATE_FORMAT(NOW(),'%Y-%m-%d 00:00:00')" +
            "AND DATE_FORMAT(NOW(),'%Y-%m-%d %T')")
    Integer intradayRealorderData();


    //查询昨天0点到昨天当前时间成交额
    @Select("SELECT SUM(amount) FROM pay_order WHERE created_at BETWEEN DATE_FORMAT(DATE_SUB(NOW(),interval 1 day),'%Y-%m-%d 00:00:00')  " +
            " AND DATE_FORMAT(DATE_SUB(NOW(),interval 1 day),'%Y-%m-%d %T') AND (`status` = 3 OR `status` = 2)")
    Long yestdayRealmoneyData();


    //查询今日0点到当前时间成交额
    @Select("SELECT SUM(amount) FROM pay_order WHERE created_at BETWEEN DATE_FORMAT(NOW(),'%Y-%m-%d 00:00:00') " +
            "AND DATE_FORMAT(NOW(),'%Y-%m-%d %T') AND (`status` = 3 OR `status` = 2)")
    Long intradayRealmoneyData();

    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,   " +
            "                   SUM(amount) as amount,   " +
            "                   COUNT(1) AS count,   " +
            "                   SUM((IF(status = 3, amount, 0))) AS successAmount,   " +
            "                   COUNT(IF(status = 3, 1, 0)) AS successCount   " +
            "            FROM pay_order   " +
            "            WHERE  date_format(created_at, '%Y%m%d') = DATE_FORMAT(NOW(),'%Y%m%d')   " +
            "            GROUP BY name;;")
    List<CartogramDTO> hourAllData();


    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,   " +
            "                   SUM(amount) as amount,   " +
            "                   SUM((IF(status = 3 OR status = 2, amount, 0))) AS successAmount   " +
            "            FROM pay_order   " +
            "            WHERE date_format(created_at, '%Y%m%d') = DATE_FORMAT(now(),'%Y%m%d')   " +
            "            GROUP BY name; ")
    List<CartogramDTO> hourData4amount();

    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,   " +
            "                   COUNT(1) AS count,   " +
            "                   SUM(IF(status = 3 OR status = 2, 1, 0)) AS successCount   " +
            "            FROM pay_order   " +
            "            WHERE date_format(created_at, '%Y%m%d') = DATE_FORMAT(now(),'%Y%m%d')   " +
            "            GROUP BY name;")
    List<CartogramDTO> hourData4count();



    @Select("SELECT DATE_FORMAT(created_at,'%m-%d') AS name, " +
            "       COUNT(*) AS count, " +
            "       SUM(amount) as amount " +
            "FROM pay_order " +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 6 day) <=date(created_at) " +
            "GROUP BY name;")
    List<CartogramDTO> sevenDayAllCount();

    @Select("SELECT DATE_FORMAT(created_at,'%m-%d') AS name, " +
            " COUNT(*) AS count, " +
            " SUM(amount) as amount " +
            "FROM pay_order " +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 6 day) <=date(created_at) AND IF(status = 3 OR status = 2, 1, 0) " +
            "GROUP BY name;")
    List<CartogramDTO> sevenDayAllAmount();


    @Select("SELECT DATE_FORMAT(created_at,'%m-%d') AS name, " +
            "       COUNT(*) AS count, " +
            "  SUM(amount) as amount " +
            "FROM pay_order " +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 13 day) <=date(created_at) " +
            "GROUP BY name;")
    List<CartogramDTO> upSevenDayAllCount();

    @Select("SELECT DATE_FORMAT(created_at,'%m-%d') AS name, " +
            "       COUNT(*) AS count, " +
            " SUM(amount) as amount "+
            "FROM pay_order " +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 13 day) <=date(created_at) AND IF(status = 3 OR status = 2, 1, 0) " +
            "GROUP BY name;")
    List<CartogramDTO> upSevenDayAllAmount();


    @Select("select  WEEKDAY(created_at)+1  AS name, " +
            "                            COUNT(*) AS count,  " +
            "                            SUM((IF (status = 2 OR status = 3 ,1,0))) AS successCount,   " +
            "                            SUM(amount) AS amount,   " +
            "                            SUM((if(status = 2 OR status = 3 ,amount,0))) as successAmount   " +
            "                         from pay_order WHERE YEARWEEK(date_format(created_at,'%Y-%m-%d')- INTERVAL 1 DAY) = YEARWEEK(now()) " +
            "                         GROUP BY name;")
    List<CartogramDTO> sevenDay4CountAndAmount();


    @Select("select  DATE_FORMAT(created_at,'%d') AS name, " +
            "    COUNT(*) AS count, " +
            "    SUM((IF (status = 2 OR status = 3 ,1,0))) AS successCount, " +
            "    SUM(amount) AS amount, " +
            "    SUM((if(status = 2 OR status = 3 ,amount,0))) as successAmount " +
            " from pay_order where DATE_FORMAT(created_at,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')  " +
            " GROUP BY name;")
    List<CartogramDTO> currentMonth4CountAndAmount();

    @Select("SELECT pay_product_name AS payname,COUNT(*) AS count, SUM(amount) as amount FROM pay_order WHERE DATE_SUB(CURDATE(),INTERVAL 7 day)<= DATE(created_at) GROUP BY pay_product_name ORDER BY amount DESC")
    List<CartogramPayDTO> payOrders();


    @Select("SELECT status,COUNT(*) as count FROM pay_order WHERE DATEDIFF(now(),created_at) = 0 group by status")
    List<CartogramPayStatusVo> PayStatuss();


}
