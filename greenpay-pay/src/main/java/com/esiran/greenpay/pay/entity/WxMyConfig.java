package com.esiran.greenpay.pay.entity;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;
@Data
public class WxMyConfig implements WXPayConfig {

    private String appId;
    private String mchId;
    private String mchKey;

    public WxMyConfig(String appId, String mchId, String mchKey) {
        this.appId = appId;
        this.mchId = mchId;
        this.mchKey = mchKey;
    }

    @Override
    public String getAppID() {
        return this.appId;
    }

    @Override
    public String getMchID() {
        return this.mchId;
    }

    @Override
    public String getKey() {
        return this.mchKey;
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}
