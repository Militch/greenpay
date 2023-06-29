package com.esiran.greenpay.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.merchant.entity.*;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.merchant.service.IPayAccountService;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.service.IOrderService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.LongStream;

@Controller
@RequestMapping
public class MerchantController extends CURDBaseController{

    private final IPayAccountService payAccountService;
    private final IPrepaidAccountService prepaidAccountService;
    private final IOrderService orderService;
    private final IMerchantService merchantService;

    public MerchantController(IPayAccountService payAccountService, IPrepaidAccountService prepaidAccountService, IOrderService orderService, IMerchantService merchantService) {
        this.payAccountService = payAccountService;
        this.prepaidAccountService = prepaidAccountService;
        this.orderService = orderService;
        this.merchantService = merchantService;
    }
    @GetMapping
    public String index(){
        return redirect("/home");
    }
    @GetMapping("/home")
    public String home(Model model){
        Merchant m = theUser();
        HomeData homeData = merchantService.homeData(m.getId());
        model.addAttribute("homeData",homeData);
        return "merchant/index";
    }
    @GetMapping("/user/profile")
    public String user(Model model){
        Merchant merchant = theUser();
        model.addAttribute("merchant",merchant);
        return "merchant/user";
    }
    @GetMapping("/user/pub/file")
    public String pubKeyFile(Model model){
        Merchant merchant = theUser();
        model.addAttribute("merchant",merchant);
        return "merchant/user";
    }
    @GetMapping("/user/security")
    public String security(Model model){
        Merchant merchant = theUser();
        MerchantDetailDTO merchantDetailDTO = merchantService.findMerchantById(merchant.getId());
        model.addAttribute("merchant", merchantDetailDTO);
        return "merchant/usersecurity";
    }
    @GetMapping("/user/download/rsa/{filename}")
    public void downloadRsa(@PathVariable String filename, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(filename)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Merchant merchant = theUser();
        MerchantDetailDTO merchantDetailDTO = merchantService.findMerchantById(merchant.getId());
        if (merchantDetailDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String pubKeyVal = merchantDetailDTO.getApiConfig().getPubKeyVal();
        String mchPubKeyVal = merchantDetailDTO.getApiConfig().getMchPubKeyVal();
        OutputStream os = response.getOutputStream();
        if (filename.equals("api_pub_key.pem") && !StringUtils.isEmpty(pubKeyVal)){
            os.write(pubKeyVal.getBytes());
        }else if(filename.equals("public_key.pem") && !StringUtils.isEmpty(mchPubKeyVal)){
            os.write(mchPubKeyVal.getBytes());
        }else{
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",String.format("attachment; filename=%s",filename));
        os.flush();
        os.close();
        response.flushBuffer();
    }
    @GetMapping("/user/api")
    public String userapi(Model model) {
        Merchant merchant = theUser();
        MerchantDetailDTO merchantDetailDTO = merchantService.findMerchantById(merchant.getId());
        model.addAttribute("merchant", merchantDetailDTO);
        return "merchant/userapi";
    }

    @GetMapping("/login")
    @PageViewHandleError
    public String login() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return redirect("/merchant/home");
        }
        return "merchant/login2";
    }
    @PostMapping("/login")
    public String loginPost(@Valid UsernamePasswordInputDTO inputDTO){
        UsernamePasswordToken token = new UsernamePasswordToken(
                inputDTO.getUsername(),inputDTO.getPassword());
        SecurityUtils.getSubject().login(token);
        return redirect("/merchant/home");
    }
    @PostMapping("/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return redirect("/merchant/login");
    }

}
