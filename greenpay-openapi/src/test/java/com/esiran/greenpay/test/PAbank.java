package com.esiran.greenpay.test;


import com.esiran.greenpay.bank.pingan.entity.PATradeCode;
import com.esiran.greenpay.common.util.Map2Xml;
import com.esiran.greenpay.common.util.NumberUtil;
import com.google.gson.Gson;
import okhttp3.*;

import org.junit.Test;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

}
