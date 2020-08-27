package com.esiran.greenadmin.test;

import com.esiran.greenadmin.common.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class SimpleTest {

    private static final Gson gson = new GsonBuilder().create();
    private long taskRetry(String message){
        Map<String,String> msgObj = MapUtil.jsonString2stringMap(message);
        if (msgObj == null) return -1;
        int count = Integer.parseInt(msgObj.get("count"));
        msgObj.put("count",String.valueOf(count+1));
        return (30*(int) Math.pow(2,count-1))*1000;
    }
    @Test
    public void test(){
        long sum = 0;
        for (int i = 0; i<10; i++){
            Map<String,String> messagePayload = new HashMap<>();
            messagePayload.put("orderNo", "123456789");
            messagePayload.put("mchId", "124567890");
            messagePayload.put("count", String.valueOf(i+1));
            long t = taskRetry(gson.toJson(messagePayload));
            System.out.println(String.format("second: %s, minutes: %s, hour: %s",
                    (t/1000.0f), (t/1000.00f/60.00f),(t/1000.00f/60.00f/60.00f) ));
            sum += t;
        }
        float f = sum/1000.00f/60.00f/60.00f;
        System.out.println(f);
    }

    @Test
    public void test2() throws UnsupportedEncodingException {




//        List<Map<String,Object>> dataList = new ArrayList<>();
//        Map<String,Object> map1 = new HashMap<>();
//        map1.put("t","0520");
//        map1.put("data",250);
//        dataList.add(map1);
//        Map<String,Object> map2 = new HashMap<>();
//        map2.put("t","0521");
//        map2.put("data",250);
//        dataList.add(map2);
//        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
//        sdf.format(new Date());
//
//
//        List<Map<String,Object>> out = new ArrayList<>();
//        for (int i=0;i<7;i++){
//            long s = System.currentTimeMillis()-((1000*60*60*24) * (i));
//            String a = sdf.format(new Date(s));
//            List<Map<String,Object>> targetList = dataList.stream().filter(item->{
//                String t = (String) item.get("t");
//                return t.equals(a);
//            }).collect(Collectors.toList());
//            Map<String,Object> data = null;
//            if (targetList.size() == 0){
//                data = new HashMap<>();
//                data.put("t",a);
//                data.put("data",0);
//            }else {
//                data = targetList.get(0);
//            }
//            out.add(data);
//        }
//        System.out.println(out);


//
//        int[] day = new int[]{};
//        for (){}




        String s = "123465789";
        int i = 222 - s.length();
        char[] cs = new char[i];
        Arrays.fill(cs,'0');
        String a = s.concat(new String(cs));
        System.out.println(a);
//        String body = "<form name='fm' method='post' action='http://ggccapi.genguchangcun.cn/mfe88/codePay.php'><input type='hidden' name='orderSource' value='1' /><input type='hidden' name='orderNo' value='666031040836281' /><input type='hidden' name='signVersion' value='V3.0' /><input type='hidden' name='sign' value='Our6Rb5canP2hd6OwwNS1bqqusOuQCrjHdTtPY80xpIRY75fBPuk5WF33C23SVohe0%2F1xIyNI9EP%0D%0AOKD5DyYuvOXSdnyks%2BXb5vX0ho%2BsypatENveUzYLi28XCVXVdFdjxqjm1TP4R5Lp8dAxSR8qULoK%0D%0A0mLf0iZC6L8rqSla8JY%3D%0D%0A' /><input type='hidden' name='riskVersion' value='V3.0' /><input type='hidden' name='version' value='V3.0' /><input type='hidden' name='productName' value='666031040836281' /><input type='hidden' name='orderAmount' value='900' /><input type='hidden' name='orderTime' value='20200510230717' /><input type='hidden' name='payChannelCode' value='CX_DC' /><input type='hidden' name='service' value='getCodeUrl' /><input type='hidden' name='curCode' value='CNY' /><input type='hidden' name='merchantNo' value='JK0002958' /><input type='hidden' name='bgUrl' value='http://122.114.215.124:8972/notify' /></form><script language='JavaScript' > document.fm.submit();</script>";
//        Pattern pattern = Pattern.compile("^<form .+$",Pattern.DOTALL);
//        Matcher matcher = pattern.matcher(body);
//        boolean a = matcher.matches();
//        System.out.println(a);
//        long sum = 0;
//        for (int i = 0; i<10; i++){
//            Map<String,String> messagePayload = new HashMap<>();
//            messagePayload.put("orderNo", "123456789");
//            messagePayload.put("mchId", "124567890");
//            messagePayload.put("count", String.valueOf(i+1));
//            long t = taskRetry(gson.toJson(messagePayload));
//            System.out.println(String.format("second: %s, minutes: %s, hour: %s",
//                    (t/1000.0f), (t/1000.00f/60.00f),(t/1000.00f/60.00f/60.00f) ));
//            sum += t;
//        }
//        float f = sum/1000.00f/60.00f/60.00f;
//        System.out.println(f);
//        String abc = RSAUtil.formatKeyPem("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIIVl7aJVGduveuwxUATC2AJRy0fE5H6tiA2hNtlZE7HDhEg73n1RnMwcBYbCk0xEuunp1Fbw/dBcMrI6GYriba7ffi37gfoCxEt3la5qxx7i4839iFq1I7Jur2PWxipVdEBeo9letpsTmLg52t4oZHA5KWZK1FJnhgfgGPLnCoPAgMBAAECgYA+NfFePIL/DDkLtHhA0lrITOebLpd/YrUi5q/W9MBp5ExX6LZeTuyoPev8xmXA0M1Jod4kzIwFfWhhsn9iDURITHjM1T4K36IdLKQimtwnp3DChMJtRwqev0g8ZIkHdQqourYscq1VxOIwR97xGsH54WFQOJlopUizs+NosmzOcQJBANiZ2NSXMZEmP5mBijwL11UAuvTXSMEyr2HvrruPtz5w2FfJwH0yHwLVjE8DtMDaVFsib1dJTVc0VGZujw4708MCQQCZvww4IbpwuqpZ0ft/UJvIfjwII+CjjmK7CG9Ae2I3qgAmW+J8tsRg7b6nVBz3fu6Qz3KBQLy3eoAzqVus+qfFAkEAuUkyAPmtPxtXAHudwqvmciKDy3p2FD7FZKPh9zSDbniduMsvTGmZuZTvx4/GCcs6qhMU57ge3uA26sDcwzJOfQJAc8SLquiavq+P/jpDKcbExt9mzKpXSFC6vyLGwsMlXczAeCHQFSB6FpJucQjBFwuZD6llCzZ346B2UHBB+6pyEQJBAJJ3NlIAUQl9JgKdwjiBWiQIdV1TGP/jDw2E9Xv7yAbrrj0NLdL9bYcv2dFnl8J0yIObNvhrWdP1S2FPB3nEb0I=");
//        System.out.println(abc);

    }

}
