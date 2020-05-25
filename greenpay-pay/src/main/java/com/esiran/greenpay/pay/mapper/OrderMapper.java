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

    @Select("SELECT a.hour hour, ifnull(b.amount, 0) amount ,ifnull(b.count, 0) count,ifnull(c.sccamount,0) sucamount, ifnull(c.succount,0) succ FROM (  \n" +
            "                        SELECT 0 hour UNION ALL SELECT 1 hour UNION ALL SELECT 2 hour UNION ALL SELECT 3 hour   \n" +
            "                        UNION ALL SELECT 4 hour UNION ALL SELECT 5 hour UNION ALL SELECT 6 hour   \n" +
            "                        UNION ALL SELECT 7 hour UNION ALL SELECT 8 hour UNION ALL SELECT 9 hour   \n" +
            "                        UNION ALL SELECT 10 hour UNION ALL SELECT 11 hour UNION ALL SELECT 12 hour   \n" +
            "                        UNION ALL SELECT 13 hour UNION ALL SELECT 14 hour UNION ALL SELECT 15 hour   \n" +
            "                        UNION ALL SELECT 16 hour UNION ALL SELECT 17 hour UNION ALL SELECT 18 hour   \n" +
            "                        UNION ALL SELECT 19 hour UNION ALL SELECT 20 hour UNION ALL SELECT 21 hour   \n" +
            "                        UNION ALL SELECT 22 hour UNION ALL SELECT 23 hour  \n" +
            "                        ) a LEFT JOIN  \n" +
            "                          (  \n" +
            "                            SELECT  \n" +
            "                              hour(created_at)  hour,  \n" +
            "                              SUM(amount) amount,  \n" +
            "                              count(order_no) count  \n" +
            "                            FROM pay_order  \n" +
            "                            WHERE date_format(created_at, '%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d')  \n" +
            "                             GROUP BY date_format(created_at, '%Y%m%d-%H'), hour  \n" +
            "                          ) b   \n" +
            "                          ON a.hour=b.hour   \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\tLEFT JOIN\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t  (  \n" +
            "                            SELECT  \n" +
            "                              hour(created_at)  hour,  \n" +
            "                              SUM(amount) sccamount,  \n" +
            "                              count(order_no) succount  \n" +
            "                            FROM pay_order  \n" +
            "                            WHERE date_format(created_at, '%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d')  \n" +
            "                            AND status = 3 GROUP BY date_format(created_at, '%Y%m%d-%H'), hour  \n" +
            "                          ) c ON b.hour = c.hour\n" +
            "                         ORDER BY hour")
    List<CartogramDTO> hourData();


    @Select("select a.click_date as time,ifnull(b.count,0) as count, ifnull(c.count,0) as succ,ifnull(c.sucamount,0) as sucamount, ifnull(b.amount,0) as amount \n" +
            "                        from ( \n" +
            "                            SELECT curdate() as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 1 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 2 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 3 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 4 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 5 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 6 day) as click_date \n" +
            "                        ) a \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tleft join ( \n" +
            "                        SELECT DATE_FORMAT(created_at,'%Y-%m-%d') as time, \n" +
            "                          SUM(amount) as amount ,COUNT(DATE_FORMAT(created_at,'%Y-%m-%d')) as count  \n" +
            "                        FROM `pay_order` WHERE DATE_SUB(CURDATE(),INTERVAL 7 day) <=date(created_at)  \n" +
            "                        GROUP BY DATE_FORMAT(created_at,'%Y-%m-%d')  \n" +
            "                        ) b \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tON a.click_date = b.time left join( \n" +
            "                          SELECT DATE_FORMAT(created_at,'%Y-%m-%d') as time, \n" +
            "                         COUNT(DATE_FORMAT(created_at,'%Y-%m-%d')) as count,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t SUM(amount) as sucamount\n" +
            "                         FROM `pay_order` WHERE DATE_SUB(CURDATE(),INTERVAL 7 day) <=date(created_at) and status = 3 \n" +
            "                         GROUP BY DATE_FORMAT(created_at,'%Y-%m-%d')  \n" +
            "                        )c \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\ton b.time = c.time  \n" +
            "                         ORDER BY a.click_date\n" +
            "             ")
    List<CartogramDTO> sevenDayCartogram();


    @Select("select a.click_date as time,ifnull(b.count,0) as count, ifnull(c.count,0) as succ,ifnull(c.sucamount,0) as sucamount, ifnull(b.amount,0) as amount \n" +
            "                        from ( \n" +
            "                            SELECT date_sub(curdate(), interval 7 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 8 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 9 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 10 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 11 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 12 day) as click_date \n" +
            "                            union all \n" +
            "                            SELECT date_sub(curdate(), interval 13 day) as click_date \n" +
            "                        ) a \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tleft join ( \n" +
            "                        SELECT DATE_FORMAT(created_at,'%Y-%m-%d') as time, \n" +
            "                          SUM(amount) as amount ,COUNT(DATE_FORMAT(created_at,'%Y-%m-%d')) as count  \n" +
            "                        FROM `pay_order` WHERE DATE_SUB(CURDATE(),INTERVAL 7 day) <=date(created_at)  \n" +
            "                        GROUP BY DATE_FORMAT(created_at,'%Y-%m-%d')  \n" +
            "                        ) b \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tON a.click_date = b.time left join( \n" +
            "                          SELECT DATE_FORMAT(created_at,'%Y-%m-%d') as time, \n" +
            "                         COUNT(DATE_FORMAT(created_at,'%Y-%m-%d')) as count,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t SUM(amount) as sucamount\n" +
            "                         FROM `pay_order` WHERE DATE_SUB(CURDATE(),INTERVAL 7 day) <=date(created_at) and status = 3 \n" +
            "                         GROUP BY DATE_FORMAT(created_at,'%Y-%m-%d')  \n" +
            "                        )c \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\ton b.time = c.time  \n" +
            "                         ORDER BY a.click_date\n" +
            "             \n" +
            "            ")
    List<CartogramDTO> upSevenDayCartogram();


    @Select("SELECT pay_product_name AS payname,COUNT(*) AS count, SUM(amount) as amount FROM pay_order GROUP BY pay_product_name ORDER BY amount DESC")
    List<CartogramPayDTO> payOrders();


    @Select("SELECT status,COUNT(*) as count FROM pay_order group by status")
    List<CartogramPayStatusVo> PayStatuss();


}
