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
    @Select("SELECT count(order_no) FROM pay_order WHERE DATEDIFF(now(),created_at) = 0 AND `status` = 3")
    Integer findIntradayOrderSucc();

    //查询昨天成功订单总数
    @Select("SELECT count(order_no) FROM pay_order WHERE DATEDIFF(now(),created_at) = 1 AND `status` = 1")
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
    @Select("SELECT SUM(amount) FROM pay_order WHERE created_at BETWEEN DATE_FORMAT(DATE_SUB(NOW(),interval 1 day),'%Y-%m-%d 00:00:00')" +
            "AND DATE_FORMAT(DATE_SUB(NOW(),interval 1 day),'%Y-%m-%d %T')")
    Long yestdayRealmoneyData();


    //查询今日0点到当前时间成交额
    @Select("SELECT SUM(amount) FROM pay_order WHERE created_at BETWEEN DATE_FORMAT(NOW(),'%Y-%m-%d 00:00:00')" +
            "AND DATE_FORMAT(NOW(),'%Y-%m-%d %T')")
    Long intradayRealmoneyData();

//    @Select("SELECT a.hour hour, ifnull(b.amount, 0) amount ,ifnull(b.count, 0) count,ifnull(c.sccamount,0) sucamount, ifnull(c.succount,0) succ FROM (  \n" +
//            "                        SELECT 0 hour UNION ALL SELECT 1 hour UNION ALL SELECT 2 hour UNION ALL SELECT 3 hour   \n" +
//            "                        UNION ALL SELECT 4 hour UNION ALL SELECT 5 hour UNION ALL SELECT 6 hour   \n" +
//            "                        UNION ALL SELECT 7 hour UNION ALL SELECT 8 hour UNION ALL SELECT 9 hour   \n" +
//            "                        UNION ALL SELECT 10 hour UNION ALL SELECT 11 hour UNION ALL SELECT 12 hour   \n" +
//            "                        UNION ALL SELECT 13 hour UNION ALL SELECT 14 hour UNION ALL SELECT 15 hour   \n" +
//            "                        UNION ALL SELECT 16 hour UNION ALL SELECT 17 hour UNION ALL SELECT 18 hour   \n" +
//            "                        UNION ALL SELECT 19 hour UNION ALL SELECT 20 hour UNION ALL SELECT 21 hour   \n" +
//            "                        UNION ALL SELECT 22 hour UNION ALL SELECT 23 hour  \n" +
//            "                        ) a LEFT JOIN  \n" +
//            "                          (  \n" +
//            "                            SELECT  \n" +
//            "                              hour(created_at)  hour,  \n" +
//            "                              SUM(amount) amount,  \n" +
//            "                              count(order_no) count  \n" +
//            "                            FROM pay_order  \n" +
//            "                            WHERE date_format(created_at, '%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d')  \n" +
//            "                             GROUP BY date_format(created_at, '%Y%m%d-%H'), hour  \n" +
//            "                          ) b   \n" +
//            "                          ON a.hour=b.hour   \n" +
//            "\t\t\t\t\t\t\t\t\t\t\t\t\tLEFT JOIN\n" +
//            "\t\t\t\t\t\t\t\t\t\t\t\t\t  (  \n" +
//            "                            SELECT  \n" +
//            "                              hour(created_at)  hour,  \n" +
//            "                              SUM(amount) sccamount,  \n" +
//            "                              count(order_no) succount  \n" +
//            "                            FROM pay_order  \n" +
//            "                            WHERE date_format(created_at, '%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d')  \n" +
//            "                            AND status = 3 GROUP BY date_format(created_at, '%Y%m%d-%H'), hour  \n" +
//            "                          ) c ON b.hour = c.hour\n" +
//            "                         ORDER BY hour")

    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,\n" +
            "       SUM(amount) as amount,\n" +
            "       COUNT(1) AS count,\n" +
            "       SUM((IF(status = 3, amount, 0))) AS successAmount,\n" +
            "       COUNT(IF(status = 3, 1, 0)) AS successCount\n" +
            "FROM pay_order\n" +
            "WHERE order_no\n" +
            "  AND date_format(created_at, '%Y%m%d') = DATE_FORMAT(NOW(),'%Y%m%d')\n" +
            "GROUP BY name;")
    List<CartogramDTO> hourData();


    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,\n" +
            "       SUM(amount) as amount,\n" +
            "       SUM((IF(status = 3 OR status = 2, amount, 0))) AS successAmount\n" +
            "FROM pay_order\n" +
            "WHERE date_format(created_at, '%Y%m%d') = DATE_FORMAT(now(),'%Y%m%d')\n" +
            "GROUP BY name;\n")
    List<CartogramDTO> hourData4amount();

    @Select("SELECT DATE_FORMAT(created_at,'%H') AS name,\n" +
            "       COUNT(1) AS count,\n" +
            "       COUNT(IF(status = 3 OR status = 2, 1, 0)) AS successCount\n" +
            "FROM pay_order\n" +
            "WHERE date_format(created_at, '%Y%m%d') = DATE_FORMAT(now(),'%Y%m%d')\n" +
            "GROUP BY name;")
    List<CartogramDTO> hourData4count();



    @Select("SELECT DATE_FORMAT(created_at,'%Y%m%d') AS name,\n" +
            "       COUNT(*) AS count,\n" +
            "\t\t\t SUM(amount) as amount\n" +
            "\t\t\t \n" +
            "FROM pay_order\n" +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 6 day) <=date(created_at)\n" +
            "GROUP BY name;")
    List<CartogramDTO> sevenDayAllCount();

    @Select("SELECT DATE_FORMAT(created_at,'%Y%m%d') AS name,\n" +
            " COUNT(*) AS count,\n" +
            " SUM(amount) as amount\n" +
            "FROM pay_order\n" +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 6 day) <=date(created_at) AND IF(status = 3 OR status = 2, 1, 0)\n" +
            "GROUP BY name;")
    List<CartogramDTO> sevenDayAllAmount();


    @Select("SELECT DATE_FORMAT(created_at,'%Y%m%d') AS name,\n" +
            "       COUNT(*) AS count,\n" +
            "\t\t\t SUM(amount) as amount\n" +
            "FROM pay_order\n" +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 13 day) <=date(created_at)\n" +
            "GROUP BY name;")
    List<CartogramDTO> upSevenDayAllCount();

    @Select("SELECT DATE_FORMAT(created_at,'%Y%m%d') AS name,\n" +
            "       COUNT(*) AS count,\n" +
            "\t\t\t SUM(amount) as amount\n" +
            "\t\t\t \n" +
            "FROM pay_order\n" +
            "WHERE  DATE_SUB(CURDATE(),INTERVAL 13 day) <=date(created_at) AND IF(status = 3 OR status = 2, 1, 0)\n" +
            "GROUP BY name;")
    List<CartogramDTO> upSevenDayAllAmount();


    @Select("SELECT pay_product_name AS payname,COUNT(*) AS count, SUM(amount) as amount FROM pay_order GROUP BY pay_product_name ORDER BY amount DESC")
    List<CartogramPayDTO> payOrders();


    @Select("SELECT status,COUNT(*) as count FROM pay_order WHERE DATEDIFF(now(),created_at) = 0 group by status")
    List<CartogramPayStatusVo> PayStatuss();


}
