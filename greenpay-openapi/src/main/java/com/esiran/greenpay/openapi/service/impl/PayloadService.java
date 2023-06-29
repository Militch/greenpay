package com.esiran.greenpay.openapi.service.impl;

import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.openapi.service.IPayloadService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PayloadService implements IPayloadService {
    private static final Gson gson = new GsonBuilder().create();
    private final StringRedisTemplate redisTemplate;
    public PayloadService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String savePayload2cache(String cacheKey, Map<String,String> payload) {
        String payloadJson = gson.toJson(payload);
        String payloadId = String.valueOf(System.currentTimeMillis());
        payloadId = EncryptUtil.md5(payloadId);
        redisTemplate.opsForValue().set(cacheKey,payloadJson);
        return payloadId;
    }

    @Override
    public String savePayload2cache(
            String cacheKey, Map<String, String> payload,
            long t, TimeUnit timeUnit) {
        String payloadJson = gson.toJson(payload);
        String payloadId = String.valueOf(System.currentTimeMillis());
        payloadId = EncryptUtil.md5(payloadId);
        String key = String.format("%s:%s",cacheKey,payloadId);
        redisTemplate.opsForValue().set(key,payloadJson,t,timeUnit);
        return payloadId;
    }

    @Override
    public Map<String, String> getPayload4cache(String cacheKey, String payloadId) {
        String key = String.format("%s:%s",cacheKey,payloadId);
        String payloadJson = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(payloadJson)) return null;
        return gson.fromJson(payloadJson,new TypeToken<Map<String,String>>(){}.getType());
    }
}
