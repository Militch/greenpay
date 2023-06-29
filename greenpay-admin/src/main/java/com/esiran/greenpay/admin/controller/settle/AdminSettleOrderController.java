package com.esiran.greenpay.admin.controller.settle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;
import com.esiran.greenpay.pay.entity.Type;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.settle.entity.SettleConfig;
import com.esiran.greenpay.settle.entity.SettleConfigInputDTO;
import com.esiran.greenpay.settle.entity.SettleOrderDTO;
import com.esiran.greenpay.settle.entity.SettleOrderInputDTO;
import com.esiran.greenpay.settle.service.ISettleConfigService;
import com.esiran.greenpay.settle.service.ISettleOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/settle")
public class AdminSettleOrderController extends CURDBaseController {
    private final ISettleOrderService orderService;
    private final ISettleConfigService settleConfigService;
    private final IOrderDetailService orderDetailService;
    public AdminSettleOrderController(
            ISettleOrderService orderService, ISettleConfigService settleConfigService, IOrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.settleConfigService = settleConfigService;
//        this.settleOrderService = settleOrderService;
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/list/{orderNo}/settlement")
    @PageViewHandleError
    public String replenishment(@PathVariable String orderNo, ModelMap modelMap){
        SettleOrderDTO order = orderService.getByOrderNo(orderNo);
        OrderDetailDTO orderDetail = orderDetailService.getByOrderNo(orderNo);
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Type type = new Type();
            type.setStatus(false);
            type.setType(i);
            type.setTypeCode("type_" + i);
            type.setTypeName("选择项" + i);
            types.add(type);
        }
        modelMap.addAttribute("interface_type", types);
        modelMap.addAttribute("order", order);
        return "admin/settle/payable/edit";
    }


    @GetMapping("/payable")
    @PageViewHandleError
    public String list(HttpServletRequest request, ModelMap modelMap) {

        String qs = request.getQueryString();
        Map<String, String> qm = MapUtil.httpQueryString2map(qs);
        if (qm != null) {
            String qss  = MapUtil.map2httpQuery(qm);
            modelMap.put("qs", qss);
        }
        return "admin/settle/payable/list";
    }

    @PostMapping("/payable")
    public String settlement (@RequestParam String action ,@RequestParam String orderNo) throws PostResourceException {
        SettleOrderDTO byOrderNo = orderService.getByOrderNo(orderNo);
        //状态（1：待审核，2：待处理，3：处理中，4：已结算，-1：已驳回，-2：结算失败）
        switch (byOrderNo.getStatus()) {
            case -1:
                throw new PostResourceException("订单状态:已驳回");
            case -2:
                throw new PostResourceException("订单状态:结算失败");
            case 1:
                throw new PostResourceException("订单状态:待审核");
            case 3:
                throw new PostResourceException("订单状态:处理中");
            case 4:
                throw new PostResourceException("订单状态:已结算");
            default:
                break;
        }

        switch (action) {
            case "settlement":

                orderService.updateOrderStatus(orderNo, 4);
                break;
            default:
                break;
        }

        return "redirect:/admin/settle/payable";
    }

    @GetMapping("/audit")
    @PageViewHandleError
    public String audit(HttpServletRequest request,ModelMap modelMap) {
        String qs = request.getQueryString();
        Map<String, String> qm = MapUtil.httpQueryString2map(qs);
        if (qm != null) {
            String qss  = MapUtil.map2httpQuery(qm);
            modelMap.put("qs", qss);
        }
        return "admin/settle/audit/list";
//        return "admin/settle/audit/addTemp";
    }
    @PostMapping("/audit")
    public String auditPost(@RequestParam String orderNo,@RequestParam String action) throws PostResourceException {
        switch (action) {
            case "pass":
                orderService.updateOrderStatus(orderNo,2);
                break;
            case "nopass":
                orderService.updateOrderStatus(orderNo,-1);
                break;
            default:
                break;
        }

        return redirect("/admin/settle/audit");
    }

    //测试订单提交

//    private final ISettleOrderService settleOrderService;
//    @ResponseBody
//    @PostMapping("/audit")
//    public Map payExtract(@Validated SettleOrderInputDTO settleOrderInputDTO) throws ResourceNotFoundException, PostResourceException {
//        settleOrderService.postOrder(settleOrderInputDTO);
//        Map m = new HashMap<String,String>();
//        m.put("code","1");
//        m.put("msg","提交成功");
//        return m;
//    }


    @GetMapping("/settings")
    @PageViewHandleError
    public String settings(ModelMap modelMap, HttpSession httpSession) {
        LambdaQueryWrapper<SettleConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("LIMIT 1");
        SettleConfig settleConfig = settleConfigService.getOne(queryWrapper);
        modelMap.addAttribute("data", settleConfig==null?new SettleConfig():settleConfig);
        return "admin/settle/settings/index";
    }


    @PostMapping("/settings")
    public String settingsPost(@Valid SettleConfigInputDTO dto){
        settleConfigService.update(dto);
        return redirect("/admin/settle/settings");
    }
    @GetMapping("/audit/{orderNo}/detail")
    public String auditDetail(@PathVariable String orderNo, ModelMap modelMap){
        SettleOrderDTO order = orderService.getByOrderNo(orderNo);
        modelMap.addAttribute("order", order);
        return "admin/settle/audit/detail";
    }

    @GetMapping("/payable/{orderNo}/detail")
    public String detail(@PathVariable String orderNo, ModelMap modelMap){
        SettleOrderDTO order = orderService.getByOrderNo(orderNo);
        modelMap.addAttribute("order", order);
        return "admin/settle/payable/detail";
    }

}
