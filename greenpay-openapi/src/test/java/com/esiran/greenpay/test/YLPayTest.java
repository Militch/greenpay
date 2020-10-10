package com.esiran.greenpay.test;

import com.esiran.greenpay.agentpay.entity.MsgBean;
import com.esiran.greenpay.agentpay.entity.MsgBody;
import com.esiran.greenpay.agentpay.util.Base64;
import com.esiran.greenpay.agentpay.util.RSA;
import com.esiran.greenpay.agentpay.util.TripleDes;
import com.esiran.greenpay.agentpay.util.Util;
import com.google.common.base.Strings;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class YLPayTest {

//    private static String dna_pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqWSfUW3fSyoOYzOG8joy3xldpBanLVg8gEDcvm9KxVjqvA/qJI7y0Rmkc1I7l9vAfWtNzphMC+wlulpaAsa/4PbfVj+WhoNQyhG+m4sP27BA8xuevNT9/W7/2ZVk4324NSowwWkaqo1yuZe1wQMcVhROz2h+g7j/uZD0fiCokWwIDAQAB";//测试环境
    private static String dna_pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc+L2JGUKlGtsFm2f/wuF2T6/8mc6yrN8tLPgsx7sxAatvMvunHLXKC8xjkChHqVfJgohV4OIWe8zCw7jPsJMiPvrNnFHJ2Mumg/zQ8eZOnzMA0LDqBNFvZnOpy2XtagQn4yxxzG9+9h4P5eNojC3vD2t3H/6q5V3Cd022/egIZQIDAQAB"; //生产环境
//    private static String mer_pfx_key = "G:\\yilian.pfx";
    private static String mer_pfx_key = "G:\\104000000253070-Encipherment.pfx";
    private static String mer_pfx_pass = "02205296";
    private static String url = "https://agent.payeco.com/service";
//    private static String url = "https://testagent.payeco.com:9444/service";
    private static final OkHttpClient okHttpClient;
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }

    //代付提交
    @Test
    public void test1() throws Exception {
        MsgBean req_bean = new MsgBean();
        req_bean.setVERSION("2.1");
        req_bean.setMSG_TYPE("100001");
        req_bean.setBATCH_NO(new String(Base64.decode(Util.generateKey(99999,8))));//每笔订单不可重复，建议：公司简称缩写+yymmdd+流水号
        req_bean.setUSER_NAME("13728096874");//系统后台登录名

        MsgBody body = new MsgBody();
        body.setSN("00000000056");//流水号，同一批次不重复即可
        body.setACC_NO("6222023602076055577");
        body.setACC_NAME("李四");
        body.setAMOUNT("1");
		/*body.setACC_PROVINCE("上海市");
		body.setACC_CITY("上海市");*/
        body.setBANK_NAME("交通银行");
        body.setACC_PROP("0");
        body.setMER_ORDER_NO("DF1234567811");
        req_bean.getBODYS().add(body);

		/*MsgBody body2 = new MsgBody();
		body2.setSN("0000000000000002");
		body2.setACC_NO("6013821900046267618");
		body2.setACC_NAME("李氏2");
		body2.setAMOUNT("256.58");
		body2.setBANK_NAME("中国银行股份有限公司广州天文苑支行");
		req_bean.getBODYS().add(body2);

		MsgBody body3 = new MsgBody();
		body3.setSN("0000000000000003");
		body3.setACC_NO("6228480082238310112");
		body3.setACC_NAME("王午3");
		body3.setAMOUNT("11.2");
		body3.setBANK_NAME("农业银行同福东路支行");
		req_bean.getBODYS().add(body3);*/

        String res = sendAndRead(signANDencrypt(req_bean));

        MsgBean res_bean = decryptANDverify(res);

        if("0000".equals(res_bean.getTRANS_STATE())) {
            System.out.println("请求成功");
        }
        System.out.println(res_bean.toXml());


    }

    //代付查询
    @Test
    public void test2(){
        MsgBean req_bean = new MsgBean();
        req_bean.setVERSION("2.1");
        req_bean.setMSG_TYPE("600001");
        req_bean.setBATCH_NO("EC997053");//同代付交易请求批次号
//        req_bean.setUSER_NAME("13574837813");//系统后台登录名 测试
        req_bean.setUSER_NAME("13728096874");//系统后台登录名 生产

//		MsgBody body = new MsgBody();
//		body.setQUERY_NO_FLAG("0");
//		body.setMER_ORDER_NO("DF123456789");
//		req_bean.getBODYS().add(body);
        String res = sendAndRead(signANDencrypt(req_bean));

        MsgBean res_bean = decryptANDverify(res);
        List<MsgBody> bodys = res_bean.getBODYS();
        if("0000".equals(res_bean.getTRANS_STATE())) {
            System.out.println("请求成功");
        }
        MsgBody msgBody = bodys.get(0);
        System.out.println(msgBody.getAMOUNT());
        System.out.println(res_bean.toXml());
    }

    private static MsgBean decryptANDverify(String res) {
        String msg_sign_enc = res.split("\\|")[0];
        String key_3des_enc = res.split("\\|")[1];
        String key_3des = RSA.decrypt(key_3des_enc, mer_pfx_key, mer_pfx_pass);
        System.out.println("key_3des : " + key_3des);
        String msg_sign = TripleDes.decrypt(key_3des, msg_sign_enc);
        System.out.println("msg_sign : "+ msg_sign);
        MsgBean res_bean = new MsgBean();
        res_bean.toBean(msg_sign);
        System.out.println("res:" + res_bean.toXml());
        String dna_sign_msg = res_bean.getMSG_SIGN();
        res_bean.setMSG_SIGN("");
        String verify = Strings.isNullOrEmpty(res_bean.getVERSION()) ? res_bean.toXml() : res_bean.toSign();
        System.out.println("verify:" + verify);
        if (!RSA.verify(dna_sign_msg, dna_pub_key, verify)) {
            System.out.println("验签失败");
            res_bean.setTRANS_STATE("00A0");
        }

        return res_bean;
    }

    private static String signANDencrypt(MsgBean req_bean) {
        System.out.println("before sign xml ==" + req_bean.toSign());
        System.out.println("msg sign = " + RSA.sign(req_bean.toSign(), mer_pfx_key, mer_pfx_pass));
        req_bean.setMSG_SIGN(RSA.sign(req_bean.toSign(), mer_pfx_key, mer_pfx_pass));
        System.out.println("req:" + req_bean.toXml());
        String key = Util.generateKey(9999, 24);
        System.out.println("key:" + key);
        String req_body_enc = TripleDes.encrypt(key, req_bean.toXml());
        System.out.println("req_body_enc:" + req_body_enc);
        String req_key_enc = RSA.encrypt(key, dna_pub_key);
        System.out.println("req_key_enc:" + req_key_enc);
        System.out.println("signANDencrypt:" + req_body_enc + "|" + req_key_enc);
        return req_body_enc + "|" + req_key_enc;
    }

    public static String sendAndRead(String req) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml; charset=utf-8"), req);
        Request builder = new Request.Builder().url(url).post(requestBody).build();
        Response response;
        try {
            response = okHttpClient.newCall(builder).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                System.out.println("支付渠道请求失败 CHANNEL_REQUEST_ERROR");
            }
            String body = responseBody.string();
            System.out.println("Response body: " + body);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            HttpURLConnection connect = (new SslConnection()).openConnection(url);
//            connect.setReadTimeout(30000);
//            connect.setConnectTimeout(10000);
//            connect.setRequestMethod("POST");
//            connect.setDoInput(true);
//            connect.setDoOutput(true);
//            connect.connect();
//            byte[] put = req.getBytes("UTF-8");
//            connect.getOutputStream().write(put);
//            connect.getOutputStream().flush();
//            connect.getOutputStream().close();
//            String res = SslConnection.read(connect);
//            connect.getInputStream().close();
//            connect.disconnect();
//            return res;
//        } catch (Exception var4) {
//            logger.error(Strings.getStackTrace(var4));
//            return "";
//        }
        return null;
    }
}
