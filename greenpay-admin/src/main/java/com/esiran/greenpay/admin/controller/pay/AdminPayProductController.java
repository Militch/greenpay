package com.esiran.greenpay.admin.controller.pay;

import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.service.*;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.service.IUserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pay/product")
public class AdminPayProductController extends CURDBaseController {
    private final IUserService userService;
    private final ITypeService typeService;
    private final IProductService productService;
    private final IPassageService passageService;
    private final IPassageAccountService passageAccountService;
    private final IProductPassageService productPassageService;
    private static final Gson gson = new GsonBuilder().create();
    public AdminPayProductController(
            IUserService userService, ITypeService typeService,
            IProductService productService,
            IPassageService passageService,
            IPassageAccountService passageAccountService, IProductPassageService productPassageService) {
        this.userService = userService;
        this.typeService = typeService;
        this.productService = productService;
        this.passageService = passageService;
        this.passageAccountService = passageAccountService;
        this.productPassageService = productPassageService;
    }

    @GetMapping("/list")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String list(HttpServletRequest httpServletRequest, ModelMap modelMap) {
        String queryString = httpServletRequest.getQueryString();
        Map<String, String> qm = MapUtil.httpQueryString2map(queryString);
        if (qm != null && qm.size()>0) {
            String s = qm.get("queryData");
            if (StringUtils.isNumeric(s)){
                qm.put("id", s);
            }else {
                qm.put("productName",s);
            }
            String qsall = MapUtil.map2httpQuery(qm);
            modelMap.put("qs",qsall);
        }
        return "admin/pay/product/list";
    }
    @PostMapping(value = "/list")
    public String listPost(@RequestParam String action, @RequestParam String ids,@RequestParam String supplyPass) throws PostResourceException {
        if (action.equals("del")){
            User user = theUser();
            boolean pass = userService.verifyTOTPPass(user.getId(), supplyPass);
            try {
                if (!pass) {
                    throw new IllegalArgumentException("动态密码校验失败");
                }
            }catch (Exception e){
                throw new PostResourceException(e.getMessage());
            }
            List<Integer> allIds = gson.fromJson(ids,new TypeToken<List<Integer>>(){}.getType());
            productService.delByIds(allIds);
        }
        return redirect("/admin/pay/product/list");
    }
    @GetMapping("/list/add")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String add(HttpSession httpSession, ModelMap modelMap){
        List<Type> availableTypes = typeService.list();
        modelMap.addAttribute("availableTypes",availableTypes);
        return "admin/pay/product/add";
    }
    @PostMapping("/list/add")
    public String addPost(@Valid ProductInputDTO productInputDTO) throws PostResourceException {
        productService.add(productInputDTO);
        return redirect("/admin/pay/product/list");
    }


    @GetMapping("/list/{id}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String edit(@PathVariable Integer id, HttpSession httpSession, ModelMap modelMap ){
        Product product = productService.getById(id);
        List<Type> availTypes = typeService.list();
        List<Passage> availPassages = passageService.listByPayTypeCode(product.getPayTypeCode());
        String availPassagesJson = gson.toJson(availPassages);
        List<PassageAccount> availPassagesAcc = passageAccountService.listByPayTypeCode(product.getPayTypeCode());
        List<ProductPassage> usagePassages = productPassageService.listByProductId(product.getId());
        String usagePassagesJson = gson.toJson(usagePassages);
        modelMap.addAttribute("availTypes",availTypes);
        modelMap.addAttribute("availPassages",availPassages);
        modelMap.addAttribute("availPassagesJson",availPassagesJson);
        modelMap.addAttribute("availPassagesAcc",availPassagesAcc);
        modelMap.addAttribute("usagePassagesJson",usagePassagesJson);
        modelMap.addAttribute("data",product);
        return "admin/pay/product/edit";
    }

    @PostMapping("/list/{id}/edit")
    public String editPost(@PathVariable Integer id, @Valid ProductInputDTO productInputDTO ) throws PostResourceException, ResourceNotFoundException {
        productService.updateById(id,productInputDTO);
        return redirect("/admin/pay/product/list/%s/edit",id);
    }
}
