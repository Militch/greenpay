package com.esiran.greenpay.openapi.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface IPayloadService {
    String savePayload2cache(String cacheKey, Map<String,String> payload);
    String savePayload2cache(String cacheKey, Map<String,String> payload, long t, TimeUnit timeUnit);
    Map<String,String> getPayload4cache(String cacheKey, String payloadId);
}
