package com.esiran.greenpay.openapi.controller;

import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.sign.SignType;
import com.esiran.greenpay.common.sign.SignVerify;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.merchant.entity.ApiConfigDTO;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.openapi.entity.WxOPenIdInputDTO;
import com.esiran.greenpay.openapi.security.OpenAPISecurityUtils;
import com.esiran.greenpay.openapi.service.IPayloadService;
import com.esiran.greenpay.openapi.util.QRCodeUtil;
import com.google.zxing.WriterException;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/v1/helper/qr")
public class APIQrHelperController {

    @GetMapping(value = "/builder",produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] builder(
            @RequestParam String codeUrl,
            @RequestParam(required = false) String style) throws IOException, WriterException {
        Matcher matcher = null;
        if (!StringUtils.isEmpty(style)){
            Pattern pattern = Pattern.compile("w(\\d+?)h(\\d+?)");
            matcher = pattern.matcher(style);
        }
        int width = 200;
        int height = 200;
        if (matcher != null && matcher.matches()){
            String w = matcher.group(1);
            String h = matcher.group(2);
            width = Integer.parseInt(w);
            height = Integer.parseInt(h);
        }
        BufferedImage bi = QRCodeUtil.toBufferedImage(codeUrl,width,height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi,"png",baos);
        return baos.toByteArray();
    }
}
