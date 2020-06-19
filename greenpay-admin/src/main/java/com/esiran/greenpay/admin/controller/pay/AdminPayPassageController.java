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
@RequestMapping("/pay/passage")
public class AdminPayPassageController extends CURDBaseController {
    private final IUserService userService;
    private final IPassageService passageService;
    private final IInterfaceService interfaceService;
    private final ITypeService typeService;
    private final IPassageAccountService passageAccountService;
    private static final Gson gson = new GsonBuilder().create();
    public AdminPayPassageController(
            IUserService userService, IPassageService passageService,
            IInterfaceService interfaceService,
            ITypeService typeService, IPassageAccountService passageAccountService) {
        this.userService = userService;
        this.passageService = passageService;
        this.interfaceService = interfaceService;
        this.typeService = typeService;
        this.passageAccountService = passageAccountService;
    }

    @GetMapping("/list")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String list(HttpServletRequest httpServletRequest, ModelMap modelMap) {
        String queryString = httpServletRequest.getQueryString();
        Map<String, String> qm = MapUtil.httpQueryString2map(queryString);
        if (qm != null && qm.size()>0) {
            String s = qm.get("typeCode");
            if (StringUtils.isNumeric(s)){
                qm.put("id", s);
            }else {
                qm.put("passageName",s);
            }
            String qsall = MapUtil.map2httpQuery(qm);
            modelMap.put("qs",qsall);
        }
        return "admin/pay/passage/list";
    }
    @PostMapping(value = "/list")
    public String listPost(@RequestParam String action, @RequestParam String ids, @RequestParam String supplyPass) throws PostResourceException {
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
            passageService.delByIds(allIds);
        }
        return redirect("/admin/pay/passage/list");
    }
    @GetMapping("/list/add")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String add(ModelMap modelMap, HttpSession httpSession){
        List<Type> availableTypes = typeService.list();
        List<Interface> availableInters = interfaceService.list();
        modelMap.addAttribute("availableTypes",availableTypes);
        modelMap.addAttribute("availableInters",availableInters);
        return "admin/pay/passage/add";
    }
    @PostMapping("/list/add")
    public String addPost(@Valid PassageInputDTO dto) throws PostResourceException {
        passageService.add(dto);
        return "redirect:/admin/pay/passage/list";
    }

    @GetMapping("/list/{passageId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String edit(@PathVariable String passageId,ModelMap modelMap, HttpSession httpSession){
        Passage data = passageService.getById(passageId);
        List<Type> availableTypes = typeService.list();
        List<Interface> availableInters = interfaceService.listByPayTypeCode(data.getPayTypeCode());
        modelMap.addAttribute("data", data);
        modelMap.addAttribute("availableTypes", availableTypes);
        modelMap.addAttribute("availableInters", availableInters);
        return "admin/pay/passage/edit";
    }

    @PostMapping("/list/{passageId}/edit")
    public String editPost(@PathVariable Integer passageId, @Valid PassageInputDTO dto) throws ResourceNotFoundException, PostResourceException {
        passageService.updateById(passageId,dto);
        return redirect("/admin/pay/passage/list/%s/edit",passageId);
    }

    @GetMapping("/list/{passageId}/acc")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String listAcc(@PathVariable String passageId, ModelMap modelMap){
        modelMap.addAttribute("passageId", passageId);
        return "admin/pay/passage/acc/list";
    }
    @PostMapping(value = "/list/{passageId}/acc")
    public String listAccPost(
            @PathVariable String passageId,
            @RequestParam String action,
            @RequestParam String ids) throws PostResourceException {
        if (action.equals("del")){
            List<Integer> allIds = gson.fromJson(ids,new TypeToken<List<Integer>>(){}.getType());
            passageAccountService.delIds(allIds);
        }
        return redirect("/admin/pay/passage/list/%s/acc",passageId);
    }
    @GetMapping("/list/{passageId}/acc/add")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String addAcc(@PathVariable String passageId, ModelMap modelMap, HttpSession httpSession){
        Passage passage = passageService.getById(passageId);
        modelMap.addAttribute("passage", passage);
        return "admin/pay/passage/acc/add";
    }
    @PostMapping("/list/{passageId}/acc/add")
    public String addAccPost(@PathVariable Integer passageId, @Valid PassageAccountInputDTO dto) throws ResourceNotFoundException, PostResourceException {
        passageAccountService.add(dto);
        return redirect("/admin/pay/passage/list/%s/acc",passageId);
    }
    @GetMapping("/list/{passageId}/acc/{accId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String editAcc(@PathVariable String passageId, @PathVariable Integer accId, ModelMap modelMap, HttpSession httpSession){
        Passage passage = passageService.getById(passageId);
        PassageAccountDTO passageAccount = passageAccountService.getDTOById(accId);
        modelMap.addAttribute("passage", passage);
        modelMap.addAttribute("data",passageAccount);
        return "admin/pay/passage/acc/edit";
    }


    @PostMapping("/list/{passageId}/acc/{accId}/edit")
    public String editAccPost(
            @PathVariable Integer passageId,
            @PathVariable Integer accId,
            @Valid PassageAccountInputDTO dto) throws ResourceNotFoundException, PostResourceException {
        passageAccountService.updateById(accId,dto);
        return redirect("/admin/pay/passage/list/%s/acc/%s/edit",passageId,accId);
    }
}
