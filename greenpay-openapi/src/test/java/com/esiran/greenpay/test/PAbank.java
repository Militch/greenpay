package com.esiran.greenpay.test;


import com.esiran.greenpay.bank.pingan.entity.PATradeCode;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.util.Map2Xml;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.pay.entity.WxMyConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.Gson;
import okhttp3.*;

import org.junit.Test;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PAbank {

    private static final Gson g = new Gson();
    //单笔代付申请接口
    @Test
    public void test1() throws Exception {
        Map<String,String> map = new HashMap<>();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern("HHmmss");
        String ymd = yyyyMMdd.format(time);
        String hms = HHmmss.format(time);
        StringBuffer sb = new StringBuffer();
        map.put("OrderNumber","123456789015");
        map.put("AcctNo","15000103599403");
        map.put("BusiType","00000");
        map.put("TranAmount","10000.00");
        map.put("InAcctNo","6214851231623096");
        map.put("InAcctName","冷中平");
        String xml = Map2Xml.mapToXml(map);
        int gbk = xml.getBytes("GBK").length;
        String format = String.format("%010d", gbk);
        System.out.println(format);
        sb.append("A001")
                .append("01")
                .append("01")
                .append("02")
                .append("00901275100000003000") //外联客户代码
                .append(format) // 接收报文长度
                .append("KHKF03")
                .append("00000")
                .append("01")
                .append(ymd)
                .append(hms)
                .append("12345678996312345679")
                .append("0000000000")
                .append("0");
        String s = sb.toString();
        System.out.println(s);
        int i = 222 - s.length();
        String a = s;
        if (i > 0){
            char[] cs = new char[i];
            Arrays.fill(cs,'0');
             a = s.concat(new String(cs));
        }

       String result =  a+xml;
        System.out.println(a+xml);

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml; charset=GBK"),result);
        Request requestOk  = new Request.Builder()
                .url("http://127.0.0.1:7072")
                .post(requestBody)
                .build();
        Response response;
        try {
             response = new OkHttpClient().newCall(requestOk).execute();
            String string = response.body().string();
            System.out.println(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //单笔代付查询
    @Test
    public void test2() throws Exception {
        Map<String,String> map = new HashMap<>();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern("HHmmss");
        String ymd = yyyyMMdd.format(time);
        String hms = HHmmss.format(time);
        StringBuffer sb = new StringBuffer();
        map.put("AcctNo","15000103599403");
        map.put("ReceiptType","ALL");
        map.put("SubType","ALL");
        map.put("StartDate","20200519");
        map.put("EndDate","20200531");
        map.put("StartRecord","1");
        map.put("RecordNum","10");
        String xml = Map2Xml.mapToXml(map);
        int gbk = xml.getBytes("GBK").length;
        String format = String.format("%010d", gbk);
        System.out.println(format);
        sb.append("A001")
                .append("01")
                .append("01")
                .append("02")
                .append("00901275100000003000") //外联客户代码
                .append(format) // 接收报文长度
                .append("ELC001")
                .append("00000")
                .append("01")
                .append(ymd)
                .append(hms)
                .append("12345678996312345678")
                .append("0000000000")
                .append("0");
        String s = sb.toString();
        System.out.println(s);
        int i = 222 - s.length();
        String a = s;
        if (i > 0){
            char[] cs = new char[i];
            Arrays.fill(cs,'0');
            a = s.concat(new String(cs));
        }

        String result =  a+xml;
        System.out.println(a+xml);

//        Socket socket = new Socket("127.0.0.1", 7072);
//        OutputStream os = socket.getOutputStream();
//        os.write(result.getBytes("GBK"));
//        socket.close();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml; charset=GBK"),result);
        Request requestOk  = new Request.Builder()
                .url("http://127.0.0.1:7072")
                .post(requestBody)
                .build();
        Response response;
        try {
            response = new OkHttpClient().newBuilder().readTimeout(Duration.ofSeconds(180))
                    .writeTimeout(Duration.ofSeconds(180))
                    .connectTimeout(Duration.ofSeconds(180))
                    .callTimeout(Duration.ofSeconds(180))
                    .build().newCall(requestOk).execute();
            String string = response.body().string();
            System.out.println(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4() throws Exception {
        Map<String,String> map = new HashMap<>();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern("HHmmss");
        String ymd = yyyyMMdd.format(time);
        String hms = HHmmss.format(time);
        StringBuffer sb = new StringBuffer();
        map.put("Account","15000103599403");
//        map.put("QueryDate","20200526");
//        map.put("BsnCode","4004");
        String xml = Map2Xml.mapToXml(map);
        int gbk = xml.getBytes("GBK").length;
        String format = String.format("%010d", gbk);
        System.out.println(format);
        sb.append("A001")
                .append("01")
                .append("01")
                .append("02")
                .append("00901275100000003000") //外联客户代码
                .append(format) // 接收报文长度
                .append("4001  ")
                .append("00000")
                .append("01")
                .append(ymd)
                .append(hms)
                .append("12345678996312345678")
                .append("0000000000")
                .append("0");
        String s = sb.toString();
        System.out.println(s);
        int i = 222 - s.length();
        String a = s;
        if (i > 0){
            char[] cs = new char[i];
            Arrays.fill(cs,'0');
            a = s.concat(new String(cs));
        }

        String result =  a+xml;
        System.out.println(a+xml);

//        Socket socket = new Socket("127.0.0.1", 7072);
//        OutputStream os = socket.getOutputStream();
//        os.write(result.getBytes("GBK"));
//        socket.close();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml; charset=GBK"),result);
        Request requestOk  = new Request.Builder()
                .url("http://103.126.241.222:7072")
                .post(requestBody)
                .build();
        Response response;
        try {
            response = new OkHttpClient().newBuilder().readTimeout(Duration.ofSeconds(180))
                    .writeTimeout(Duration.ofSeconds(180))
                    .connectTimeout(Duration.ofSeconds(180))
                    .callTimeout(Duration.ofSeconds(180))
                    .build().newCall(requestOk).execute();
            String string = response.body().string();
            System.out.println(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws Exception {
//        HeaderMsg headerMsg = new HeaderMsg();
//        headerMsg.setCompanyCode("00901275100000003000");
//        headerMsg.setOutOrderNumber("213123141241313");
//        PingAnApiEx apiEx = new PingAnApiEx("127.0.0.1", headerMsg);
////        OnceAgentPay onceAgentPay = new OnceAgentPay();
////        onceAgentPay.setAcctNo("15000103599403");
////        onceAgentPay.setInAcctName("冷中平");
////        onceAgentPay.setInAcctNo("6214851231623096");
////        onceAgentPay.setTranAmount("10000");
////        onceAgentPay.setOrderNumber("789456123321");
////        Map<String, String> map = apiEx.onceAgentPay(onceAgentPay);
//        QueryOnceAgentPay queryOnceAgentPay = new QueryOnceAgentPay();
//        queryOnceAgentPay.setAcctNo("15000103599403");
//        queryOnceAgentPay.setOrderNumber("789456123321");
//        Map<String, String> map = apiEx.queryOnceAgentPay(queryOnceAgentPay);
////        Map<String, String> map = apiEx.queryAmount("15000103599403");
//        System.out.println(map);
        String s = "12345678";
        int i = s.indexOf("3");
        String substring = s.substring(i+1);
        System.out.println(i);
        System.out.println(substring);
    }
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final OkHttpClient okHttpClient;
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    @Test
    public void test9() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("pay_memberid", "200646204");
        map.put("pay_orderid", "1234567");
        map.put("pay_applydate", dtf.format(LocalDateTime.now()));
        map.put("pay_bankcode", "");
        map.put("pay_notifyurl", "www.baidu.com");
        map.put("pay_callbackurl", "www.baidu.com");
        map.put("pay_amount", "10");
        map.put("pay_productname","测试");
        String principal = MapUtil.sortAndSerialize(map);
        String concat = principal.concat("&key=" + "xdfg2e45gnyjl47fsjbh0gddirks6e6u");
        Md5SignType signType = new Md5SignType(concat);
        String sign = signType.sign2("");
        map.put("pay_md5sign", sign);
        System.out.println(sign);
        String json = g.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url("http://pay.nbz8888.com/Pay_Index.html")
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String s = response.body().string();
        System.out.println(s);
    }
    @Test
    public void test11() throws Exception {
        WxMyConfig wxMyConfig = new WxMyConfig("appId"
                , "mchId"
                , "mchKey");
        WXPay wxPay = new WXPay(wxMyConfig);
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxMyConfig.getAppID());
        data.put("mch_id", wxMyConfig.getMchID());
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("body","");
        data.put("out_trade_no", "");//订单号
        data.put("total_fee", "");//支付金额
        data.put("spbill_create_ip", "127.0.0.1"); //自己的服务器IP地址
        data.put("notify_url", "www.baidu.com");//异步通知地址（请注意必须是外网）
        data.put("trade_type", "APP");//交易类型
        String s = WXPayUtil.generateSignature(data,wxMyConfig.getMchKey());  //签名
        data.put("sign", s);//签名
        try {
            //使用官方API请求预付订单
            Map<String, String> response = wxPay.unifiedOrder(data);
            System.out.println(response);
            String returnCode = response.get("return_code");//获取返回码
            //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
            if (returnCode.equals("SUCCESS") && response.get("result_code").equals("SUCCESS")) {
                Map<String,String> parameterMap = new HashMap<>();
                parameterMap.put("appid",response.get("appid"));
                parameterMap.put("partnerid",response.get("partnerid"));
                parameterMap.put("prepayid",response.get("prepayid"));
                parameterMap.put("package",response.get("Sign=WXPay"));
                parameterMap.put("noncestr",response.get("nonce_str"));
                parameterMap.put("timestamp",String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));
                String signature = WXPayUtil.generateSignature(parameterMap, wxMyConfig.getMchKey());
                parameterMap.put("sign",signature);
                System.out.println(parameterMap);
            } else {
                throw new APIException("","");
            }
        } catch (Exception e) {
            throw new APIException(e.getMessage(),"");
        }
    }

    @Test
    public void test99(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,2);
        String format = dateFormat.format(calendar.getTime());
        System.out.println(format);
    }

}
