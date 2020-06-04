package com.esiran.greenpay.merchant.controller.replace;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/replace")
public class MerchantReplaceController {
    @GetMapping("/replaceList")
    public String replaces(){
        return "merchant/replace/replaceList";
    }
    @GetMapping("/rechargeList")
    public String recharge(){
        return "merchant/replace/recharge";
    }
    @GetMapping("/replaces/add")
    public String replacesAdd() {
        return "merchant/replace/add";
    }
    @GetMapping("/replaces/bath")
    public String bath() {
        return "merchant/replace/bath";
    }
    @GetMapping("/recharge/add")
    public String rechargeAdd(){
        return "merchant/replace/rechargeAdd";
    }
    @GetMapping("/replaceExtract")
    public String replaceExtract(){
        return "merchant/replace/replaceExtract";
    }


    @GetMapping("/batch/list")
    public String list(){
        return "merchant/batch/batchList";
    }
}
