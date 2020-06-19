package com.esiran.greenpay.admin.controller.merchant;

import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.merchant.entity.*;
import com.esiran.greenpay.merchant.service.*;
import com.esiran.greenpay.pay.entity.Passage;
import com.esiran.greenpay.pay.entity.PassageAccount;
import com.esiran.greenpay.pay.service.IPassageAccountService;
import com.esiran.greenpay.pay.service.IPassageService;
import com.esiran.greenpay.pay.service.IProductService;
import com.esiran.greenpay.pay.service.ITypeService;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.service.IUserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/merchant")
public class AdminMerchantController extends CURDBaseController {
    private static final Gson gson = new GsonBuilder().create();
    private final IUserService userService;
    private final IMerchantService merchantService;
    private final IAgentPayOrderService agentPayOrderService;
    private final IProductService productService;
    private final IPrepaidAccountService prepaidAccountService;
    private final ITypeService typeService;
    private final IPassageService passageService;
    private final IPassageAccountService passageAccountService;
    private final IMerchantProductService merchantProductService;
    private final IMerchantProductPassageService productPassageService;
    private final IMerchantAgentPayPassageService merchantAgentPayPassageService;
    private final IAgentPayPassageService agentPayPassageService;
    public AdminMerchantController(
            IUserService userService, IMerchantService merchantService,
            IAgentPayOrderService agentPayOrderService, IProductService productService,
            IPrepaidAccountService prepaidAccountService, ITypeService typeService,
            IPassageService passageService,
            IPassageAccountService passageAccountService,
            IMerchantProductService merchantProductService,
            IMerchantProductPassageService productPassageService,
            IMerchantAgentPayPassageService merchantAgentPayPassageService,
            IAgentPayPassageService agentPayPassageService) {
        this.userService = userService;
        this.merchantService = merchantService;
        this.agentPayOrderService = agentPayOrderService;
        this.productService = productService;
        this.prepaidAccountService = prepaidAccountService;
        this.typeService = typeService;
        this.passageService = passageService;
        this.passageAccountService = passageAccountService;
        this.merchantProductService = merchantProductService;
        this.productPassageService = productPassageService;
        this.merchantAgentPayPassageService = merchantAgentPayPassageService;
        this.agentPayPassageService = agentPayPassageService;
    }

    @GetMapping("/list")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String list(){
        return "admin/merchant/list";
    }
    @GetMapping("/list/{mchId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    public String edit(@PathVariable Integer mchId, ModelMap modelMap){
        MerchantDetailDTO merchantDTO = merchantService.findMerchantById(mchId);
        modelMap.addAttribute("merchant",merchantDTO);
        return "admin/merchant/edit";
    }

    @GetMapping("/list/{mchId}/product/list")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    public String password(@PathVariable String mchId){
        return "admin/merchant/product/list";
    }

    @GetMapping("/list/{mchId}/agentpay/list")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    public String agentpay(@PathVariable String mchId,ModelMap modelMap){
        modelMap.addAttribute("mchId",mchId);
        return "admin/merchant/agentpay/list";
    }



    @GetMapping("/list/{mchId}/product/list/{productId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String product(
            @PathVariable Integer mchId,
            @PathVariable Integer productId,
            ModelMap modelMap) throws Exception {
        MerchantProductDTO merchantProduct = merchantService.selectMchProductById(mchId,productId);
        List<Passage> availPassages = passageService.listByPayTypeCode(merchantProduct.getPayTypeCode());
        List<PassageAccount> availPassagesAcc = passageAccountService.listByPayTypeCode(merchantProduct.getPayTypeCode());
        List<MerchantProductPassage> usagePassages = productPassageService.listByProductId(mchId, productId);
        String usagePassagesJson = gson.toJson(usagePassages);
        String availPassagesJson = gson.toJson(availPassages);
        modelMap.addAttribute("merchantProduct", merchantProduct);
        modelMap.addAttribute("availPassages", availPassages);
        modelMap.addAttribute("availPassagesAcc", availPassagesAcc);
        modelMap.addAttribute("availPassagesJson", availPassagesJson);
        modelMap.addAttribute("usagePassagesJson", usagePassagesJson);
        modelMap.addAttribute("mchId", mchId);
        return "admin/merchant/product/edit";
    }
    @PostMapping("/list/{mchId}/product/list/{productId}/edit")
    public String productPost(
            @PathVariable Integer mchId,
            @PathVariable Integer productId,
            @Valid MerchantProductInputDTO inputDTO) throws Exception {
        merchantProductService.updateById(inputDTO);
        return redirect("/admin/merchant/list/%s/product/list/%s/edit",mchId,productId);
    }

    @GetMapping("/add")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String add(){
        return "admin/merchant/add";
    }

    @PostMapping("/add")
    public String add(@Valid MerchantInputDTO merchant) throws Exception {
        merchantService.addMerchant(merchant);
        return redirect("/admin/merchant/list");
    }



    @GetMapping("/list/{mchId}/agentpay/list/{passageId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String agentPayPassage(
            @PathVariable Integer mchId,
            @PathVariable Integer passageId,
            ModelMap modelMap) throws Exception {
        Merchant merchant = merchantService.getById(mchId);
        if (merchant == null) throw new ResourceNotFoundException("商户不存在");
        MerchantAgentPayPassageDTO data = merchantService.selectMchAgentPayPassageByMchId(mchId, passageId);
        if (data == null) throw new ResourceNotFoundException("代付通道不存在");
        modelMap.addAttribute("mchId", mchId);
        modelMap.addAttribute("data", data);
        return "admin/merchant/agentpay/edit";
    }

    @PostMapping("/list/{mchId}/agentpay/list/{passageId}/edit")
    public String agentPayPassagePost(
            @PathVariable Integer mchId,
            @PathVariable Integer passageId,
            @Valid MerchantAgentPayPassageInputDTO dto) throws Exception {
        Merchant merchant = merchantService.getById(mchId);
        if (merchant == null) throw new ResourceNotFoundException("商户不存在");
        AgentPayPassage passage = agentPayPassageService.getById(passageId);
        if (passage == null)  throw new ResourceNotFoundException("代付通道不存在");
        merchantAgentPayPassageService.updateByInput(dto);
        return redirect("/admin/merchant/list/%s/agentpay/list/%s/edit",mchId,passageId);
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST, params = {"action=supply"})
    public String supply(@RequestParam Integer mchid, @RequestParam String supplyPass) throws PostResourceException {
        User user = theUser();
        try {
            boolean result = userService.verifyTOTPPass(user.getId(),supplyPass);
            if (!result)
                throw new IllegalArgumentException("动态密码校验失败");
        }catch (Exception e){
            throw new PostResourceException(e.getMessage());
        }

         merchantService.delMerchant(mchid);

        return redirect("list");
    }


}
