package com.esiran.greenpay.bank.pingan.api;

import com.esiran.greenpay.bank.pingan.entity.HeaderMsg;
import com.esiran.greenpay.bank.pingan.entity.OnceAgentPay;
import com.esiran.greenpay.bank.pingan.entity.PATradeCode;
import com.esiran.greenpay.bank.pingan.entity.QueryOnceAgentPay;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.Map2Xml;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PingAnApiEx {
    private final String host;
    private final HeaderMsg headerMsg;
    private static final Logger logger = LoggerFactory.getLogger(PingAnApiEx.class);
    private static final OkHttpClient okHttpClient;
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    public static Map<String, String> request(String xml, String host){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml; charset=GBK"),xml);
        Request requestOk  = new Request.Builder()
                .url(host)
                .post(requestBody)
                .build();
        Response response;
        try {
            response = okHttpClient.newCall(requestOk).execute();
            String string = response.body().string();
            logger.info("PinAnApi response : {}",string);
            String substring = string.substring(string.indexOf("<"));
            Map<String, String> map = Map2Xml.xmlToMap(substring);
            logger.info("PinAnApi response Map : {}",substring);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 格式化时间
     * @return
     */
    private static Map<String,String> time(){
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern("HHmmss");
        String ymd = yyyyMMdd.format(time);
        String hms = HHmmss.format(time);
        Map<String,String> map = new HashMap<>();
        map.put("ymd",ymd);
        map.put("hms",hms);
        return map;
    }
    /**
     * 组键报文
     * @param companyCode 企业代码
     * @param outOrderNumber 系统流水号
     * @param headerLength 报文体长度
     * @param xml 报文体
     * @param tradeCode 交易代码
     * @return
     */
    private static String header(String companyCode
            ,String outOrderNumber
            ,String headerLength
            ,String xml
            ,String tradeCode){
        StringBuilder sb = new StringBuilder();
        Map<String, String> time = time();
        if (tradeCode.length() < 6){
            tradeCode = String.format("%1$-6s", tradeCode);
        }
        sb.append("A001")
                .append("01")
                .append("01")
                .append("02")
                .append(companyCode) //外联客户代码
                .append(headerLength) // 接收报文长度
                .append(tradeCode)
                .append("00000")
                .append("01")
                .append(time.get("ymd"))
                .append(time.get("hms"))
                .append(outOrderNumber)
                .append("0000000000")
                .append("0");
        String header = sb.toString();
        logger.info("queryOnceAgentPay Message header : {}",header);
        int i = 222 - header.length();
        String a = header;
        if (i > 0){
            char[] cs = new char[i];
            Arrays.fill(cs,'0');
            a = header.concat(new String(cs));
        }
        String requestXml =  a+xml;
        logger.info("onceAgentPay requestXml : {}",requestXml);
        return requestXml;
    }

    public PingAnApiEx(String host, HeaderMsg headerMsg) {
        this.host = host;
        this.headerMsg = headerMsg;
    }
    /**
     * 单笔代付
     * @param onceAgentPay 单笔代付数据
     * @return
     * @throws Exception
     */
    public Map<String, String> onceAgentPay(OnceAgentPay onceAgentPay) throws Exception {
        String companyCode = headerMsg.getCompanyCode();
        if (companyCode == null || companyCode.length() < 20){
            throw new IllegalArgumentException("企业代码错误");
        }
        String outOrderNumber = headerMsg.getOutOrderNumber();
        if (outOrderNumber == null || outOrderNumber.length() > 20){
            throw new IllegalArgumentException("系统流水号错误");
        }
        if (outOrderNumber.length() < 20){
            outOrderNumber = String.format("%1$-20s", outOrderNumber);
        }
        if (onceAgentPay.getAcctNo() == null || onceAgentPay.getAcctNo().length() > 32){
            throw new IllegalArgumentException("企业签约帐号错误");
        }
        if (onceAgentPay.getOrderNumber() == null || onceAgentPay.getOrderNumber().length() > 20){
            throw new IllegalArgumentException("订单号错误");
        }
        Map<String,String> map = new HashMap<>();
        map.put("OrderNumber",onceAgentPay.getOrderNumber());
        map.put("AcctNo",onceAgentPay.getAcctNo());
        map.put("BusiType","00000");
        map.put("TranAmount",onceAgentPay.getTranAmount());
        map.put("InAcctNo",onceAgentPay.getInAcctNo());
        map.put("InAcctName",onceAgentPay.getInAcctName());
        String xml = Map2Xml.mapToXml(map);
        logger.info("onceAgentPay  xml : {}",xml);
        assert xml != null;
        int gbk = xml.getBytes("GBK").length;
        String format = String.format("%010d", gbk);
        String resultXML = header(companyCode, outOrderNumber, format, xml, PATradeCode.ONCE_AGENTPAY.getCode());
        return request(resultXML,host);
    }
    /**
     * 查询单笔代付结果
     * @param query 查询数据
     * @return
     * @throws Exception
     */
    public Map<String, String> queryOnceAgentPay(QueryOnceAgentPay query) throws Exception {
        String companyCode = headerMsg.getCompanyCode();
        if (companyCode == null || companyCode.length() < 20){
            throw new APIException("企业代码错误","");
        }
        String outOrderNumber = headerMsg.getOutOrderNumber();
        if (outOrderNumber == null || outOrderNumber.length() > 20){
            throw new APIException("系统流水号错误","");
        }
        if (outOrderNumber.length() < 20){
            outOrderNumber = String.format("%1$-20s", outOrderNumber);
        }
        if (query.getAcctNo() == null || query.getAcctNo().length() > 32){
            throw new APIException("企业签约帐号错误","");
        }
        if (query.getOrderNumber() == null || query.getOrderNumber().length() > 20){
            throw new APIException("订单号错误","");
        }
        Map<String,String> map = new HashMap<>();
        map.put("OrderNumber",query.getOrderNumber());
        map.put("AcctNo",query.getAcctNo());
        String xml = Map2Xml.mapToXml(map);
        logger.info("queryOnceAgentPay  xml : {}",xml);
        assert xml != null;
        int gbk = xml.getBytes("GBK").length;
        String format = String.format("%010d", gbk);
        String resultXML = header(companyCode, outOrderNumber, format, xml, PATradeCode.QUERY_ONCE_AGENTPAY.getCode());
        return request(resultXML,host);
    }
    /**
     * 查询账户余额
     * @param acctNo 企业签约账户
     * @return
     * @throws Exception
     */
    public Map<String, String> queryAmount(String acctNo) throws Exception {
        String companyCode = headerMsg.getCompanyCode();
        if (companyCode == null || companyCode.length() < 20){
            throw new APIException("企业代码错误","");
        }

        String outOrderNumber = headerMsg.getOutOrderNumber();
        if (outOrderNumber == null || outOrderNumber.length() > 20){
            throw new APIException("系统流水号错误","");
        }
        if (outOrderNumber.length() < 20){
            outOrderNumber = String.format("%1$-20s", outOrderNumber);
        }
        if (acctNo == null || acctNo.length() > 20){
            throw new APIException("企业签约帐号错误","");
        }
        Map<String,String> map = new HashMap<>();
        map.put("Account",acctNo);
        String xml = Map2Xml.mapToXml(map);
        logger.info("queryOnceAgentPay  xml : {}",xml);
        assert xml != null;
        int gbk = xml.getBytes("GBK").length;
        String format = String.format("%010d", gbk);
        String resultXML = header(companyCode, outOrderNumber, format, xml, PATradeCode.QUERY_AMOUNT.getCode());
        return request(resultXML,host);
    }

}
