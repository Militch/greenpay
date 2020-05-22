package com.esiran.greenpay.openapi.controller;

import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.sign.SignType;
import com.esiran.greenpay.common.sign.SignVerify;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.merchant.entity.ApiConfigDTO;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.openapi.entity.WxOPenIdInputDTO;
import com.esiran.greenpay.openapi.security.OpenAPISecurityUtils;
import com.esiran.greenpay.openapi.service.IPayloadService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/v1/helper/wx")
public class APIWxHelperController {
    private static final Logger logger = LoggerFactory.getLogger(APIWxHelperController.class);
    private static final String PAYLOAD_KEY_PRE = "greenpay:openapi:helper:wx:payload";
    private final IMerchantService merchantService;
    private final IApiConfigService apiConfigService;
    private final IPayloadService payloadService;
    @Value("${greenpay.web.hostname:http://localhost}")
    private String webHostname;
    public APIWxHelperController(
            IMerchantService merchantService,
            IApiConfigService apiConfigService,
            IPayloadService payloadService) {
        this.merchantService = merchantService;
        this.apiConfigService = apiConfigService;
        this.payloadService = payloadService;
    }

    private String savePayload2cache(
            String wxMpAppId,
            String wxMpSecret,
            String redirectUrl){
        if (wxMpAppId == null || wxMpSecret == null
                || redirectUrl == null){
            return null;
        }
        Map<String,String> payload = new HashMap<>();
        payload.put("wxMpAppId", wxMpAppId);
        payload.put("wxMpSecret", wxMpSecret);
        payload.put("redirectUrl",redirectUrl);
        return payloadService.savePayload2cache(PAYLOAD_KEY_PRE,payload,180,TimeUnit.SECONDS);
    }


    private String buildRedirectUrl(Map<String, String> payload, String code){
        if (payload == null) return null;
        String wxMpAppId = payload.get("wxMpAppId");
        String wxMpSecret = payload.get("wxMpSecret");
        String redirectUrl = payload.get("redirectUrl");
        if (wxMpAppId == null || wxMpSecret == null|| redirectUrl == null){
            return null;
        }
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(wxMpAppId);
        config.setSecret(wxMpSecret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        WxMpOAuth2AccessToken token;
        try {
            token = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            return null;
        }
        if (token == null) return null;
        String openId = token.getOpenId();
        Map<String,String> out = new HashMap<>();
        out.put("openId",openId);
        String principal = MapUtil.sortAndSerialize(out);
        Pattern hasArgsPattern = Pattern.compile("[a-zA-z]+://[^\\s]+?\\?[^\\s]*");
        Matcher hasArgsMatcher = hasArgsPattern.matcher(redirectUrl);
        String redirect = String.format("%s?%s",redirectUrl,principal);
        if (hasArgsMatcher.matches()){
            redirect = String.format("%s&%s",redirectUrl,principal);
        }
        return redirect;
    }

    @GetMapping("/openid")
    public String openId(
            @Valid WxOPenIdInputDTO wxOPenIdInputDTO,
            HttpServletResponse response) {
        String redirectUrl = wxOPenIdInputDTO.getRedirectUrl();
        String wxMpAppId = "wx2aeda339f56138bf";
        String wxMpSecret = "731787f51247a33c4ff210cd613dd780";
        String payloadId = savePayload2cache(wxMpAppId,wxMpSecret,redirectUrl);
        if (payloadId == null){
            response.setStatus(404);
            return null;
        }
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(wxMpAppId);
        config.setSecret(wxMpSecret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        String oauthRedirect = String.format("%s/v1/helper/wx/callback/code",webHostname);
        String state = String.format("payloadId=%s",payloadId);
        String authUrl = wxMpService.oauth2buildAuthorizationUrl(oauthRedirect,
                WxConsts.OAuth2Scope.SNSAPI_BASE,state);
        return String.format("redirect:%s",authUrl);
    }
    @GetMapping("/callback/code")
    public String callbackByCode(
            HttpServletResponse response,
            @RequestParam String code,
            @RequestParam String state) {
        Pattern pattern = Pattern.compile("payloadId=(.+)$");
        Matcher m = pattern.matcher(state);
        logger.info("Wechat oauth callback, code: {}, state: {}, m: {}",code,state,m.groupCount());
        if (!m.matches() || m.groupCount() < 1){
            response.setStatus(404);
            return null;
        }
        String payloadId = m.group(1);
        Map<String,String> payload = payloadService.getPayload4cache(PAYLOAD_KEY_PRE,payloadId);
        String redirectUrl = buildRedirectUrl(payload,code);
        if (redirectUrl == null){
            response.setStatus(404);
            return null;
        }
        return String.format("redirect:%s",redirectUrl);
    }
}
