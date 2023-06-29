package com.esiran.greenpay.admin.controller;


import com.esiran.greenpay.admin.entity.UsernamePasswordInputDTO;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.settle.service.ISettleOrderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.HashMap;

@Controller
@RequestMapping
public class AdminController extends CURDBaseController {
    private static final Gson gson = new GsonBuilder().create();
    private ISettleOrderService iSettleOrderService;
    private IMerchantService merchantService;

    public AdminController(ISettleOrderService iSettleOrderService, IMerchantService merchantService) {
        this.iSettleOrderService = iSettleOrderService;
        this.merchantService = merchantService;
    }

    @GetMapping
    public String index() {
        return redirect("/home");
    }

    @GetMapping("/home")
    public String home(Model model) {
        HashMap<String, Object> homeDate = iSettleOrderService.findHomeDate();
        String s = gson.toJson(homeDate);
        model.addAttribute("homeDataJson", s);

        return "admin/index";
    }


    @GetMapping("/merchantInfo")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    public String merchantInfo(Model model) {
        HashMap<String, Object> homeDate = merchantService.agentPayInfo();
        String s = gson.toJson(homeDate);
        model.addAttribute("homeDataJson", s);

        return "admin/merchantInfo";
    }

    @GetMapping("/login")
    @PageViewHandleError
    public String login() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return redirect("/home");
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String loginPost(@Valid UsernamePasswordInputDTO inputDTO) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                inputDTO.getUsername(), inputDTO.getPassword());
        SecurityUtils.getSubject().login(token);
        return redirect("/home");
    }

    @PostMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return redirect("/login");
    }


    @GetMapping("/unAuth")
    public String unAuth(){
        return "admin/unauth";
    }
}
