package com.esiran.greenpay.admin.controller.agentpay;

import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.agentpay.entity.*;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageAccountService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.agentpay.service.IPassageRiskService;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.esiran.greenpay.pay.entity.Type;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.esiran.greenpay.pay.service.ITypeService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
@RequestMapping("/agentpay/passage")
public class AdminAgentPayPassageController extends CURDBaseController {
    private final IAgentPayPassageService passageService;
    private final IAgentPayPassageAccountService passageAccountService;
    private final IInterfaceService interfaceService;
    private final IPassageRiskService passageRiskService;
    private final ITypeService typeService;
    private static final Gson gson = new GsonBuilder().create();
    public AdminAgentPayPassageController(
            IAgentPayPassageService passageService,
            IInterfaceService interfaceService,
            ITypeService typeService,
            IAgentPayPassageAccountService passageAccountService, IPassageRiskService passageRiskService) {
        this.passageService = passageService;
        this.interfaceService = interfaceService;
        this.typeService = typeService;
        this.passageAccountService = passageAccountService;
        this.passageRiskService = passageRiskService;
    }

    @GetMapping("/list")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String list(HttpServletRequest request,
                       ModelMap modelMap,
                       OrderQueryDTO orderQueryDTO) {
        String qs = request.getQueryString();
        Map<String,String> qm = MapUtil.httpQueryString2map(qs);
        String qss = null;
        if (qm != null){
            qss = MapUtil.map2httpQuery(qm);
        }
        modelMap.put("qs",qss);
        return "admin/agentpay/passage/list";
    }

    @PostMapping(value = "/list")
    public String listPost(@RequestParam String action, @RequestParam String ids) throws PostResourceException {
        if (action.equals("del")){
            List<Integer> allIds = gson.fromJson(ids,new TypeToken<List<Integer>>(){}.getType());
            passageService.delIds(allIds);
        }
        return redirect("/admin/agentpay/passage/list");
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
        return "admin/agentpay/passage/add";
    }
    @PostMapping("/list/add")
    public String addPost(@Valid AgentPayPassageInputDTO dto) throws PostResourceException {
        passageService.add(dto);
        return redirect("/admin/agentpay/passage/list");
    }

    @GetMapping("/list/{passageId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String edit(@PathVariable String passageId,ModelMap modelMap, HttpSession httpSession){
        AgentPayPassage data = passageService.getById(passageId);
        List<Type> availableTypes = typeService.list();
        List<Interface> availableInters = interfaceService.listByPayTypeCode(data.getPayTypeCode());
        modelMap.addAttribute("data", data);
        modelMap.addAttribute("availableTypes", availableTypes);
        modelMap.addAttribute("availableInters", availableInters);
        return "admin/agentpay/passage/edit";
    }

    @PostMapping("/list/{passageId}/edit")
    public String editPost(@PathVariable Integer passageId, @Valid AgentPayPassageInputDTO dto) throws ResourceNotFoundException, PostResourceException {
        passageService.updateById(passageId,dto);
        return redirect("/admin/agentpay/passage/list/%s/edit",passageId);
    }

    @GetMapping("/list/{passageId}/acc")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String listAcc(@PathVariable String passageId, ModelMap modelMap){
        modelMap.addAttribute("passageId", passageId);
        return "admin/agentpay/passage/acc/list";
    }

    @PostMapping("/list/{passageId}/acc")
    public String accListPost(
            @RequestParam String action,
            @RequestParam String ids,
            @PathVariable String passageId
    ) throws PostResourceException {
        if (action.equals("del")){
            List<Integer> allIds = gson.fromJson(ids,new TypeToken<List<Integer>>(){}.getType());
            passageAccountService.delByIds(allIds);
        }
        return redirect("/admin/agentpay/passage/list/%s/acc",passageId);
    }
    @GetMapping("/list/{passageId}/acc/add")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String addAcc(@PathVariable String passageId, ModelMap modelMap, HttpSession httpSession){
        AgentPayPassage passage = passageService.getById(passageId);
        modelMap.addAttribute("passage", passage);
        return "admin/agentpay/passage/acc/add";
    }
    @PostMapping("/list/{passageId}/acc/add")
    public String addAccPost(@PathVariable Integer passageId, @Valid AgentPayPassageAccountInputDTO dto) throws ResourceNotFoundException, PostResourceException {
        passageAccountService.add(dto);
        return redirect("/admin/agentpay/passage/list/%s/acc",passageId);
    }
    @GetMapping("/list/{passageId}/acc/{accId}/edit")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String editAcc(@PathVariable String passageId, @PathVariable String accId, ModelMap modelMap, HttpSession httpSession){
        AgentPayPassage passage = passageService.getById(passageId);
        AgentPayPassageAccount passageAccount = passageAccountService.getById(accId);
        modelMap.addAttribute("passage", passage);
        modelMap.addAttribute("data",passageAccount);
        return "admin/agentpay/passage/acc/edit";
    }


    @PostMapping("/list/{passageId}/acc/{accId}/edit")
    public String editAccPost(
            @PathVariable Integer passageId,
            @PathVariable Integer accId,
            @Valid AgentPayPassageAccountInputDTO dto) throws ResourceNotFoundException, PostResourceException {
        passageAccountService.updateById(accId,dto);
        return redirect("/admin/agentpay/passage/list/%s/acc/%s/edit",passageId,accId);
    }

    @GetMapping("/list/{passageId}/risk")
    @RequiresRoles("admin")
    @RequiresPermissions("system:admin")
    @PageViewHandleError
    public String risk(@PathVariable String passageId, ModelMap modelMap){
        PassageRisk passageRisk = passageRiskService.getByPassageId(Integer.valueOf(passageId));
        PassageRiskDTO dto = PassageRiskDTO.convertOrderEntity(passageRisk);
        modelMap.addAttribute("risk",dto);
        return "admin/agentpay/passage/risk";
    }


    @PostMapping("/list/{passageId}/risk")
    @PageViewHandleError
    public String riskpsot(@PathVariable String passageId, @Valid PassageRiskInputDTO inputDTO) throws ResourceNotFoundException, PostResourceException {
        passageRiskService.updateRisk(Integer.valueOf(passageId),inputDTO);
        return redirect("/admin/agentpay/passage/list/%s/risk",passageId);
    }
}
