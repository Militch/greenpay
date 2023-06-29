package com.esiran.greenpay.merchant.controller.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.merchant.controller.CURDBaseController;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.service.*;
import com.esiran.greenpay.settle.entity.SettleOrder;
import com.esiran.greenpay.settle.entity.SettleOrderDTO;
import com.esiran.greenpay.settle.entity.SettleOrderInputDTO;
import com.esiran.greenpay.settle.service.ISettleOrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiMerchantOrderController extends CURDBaseController {


    private final IOrderService payOrderService;
    private final IExtractService extractService;
    private final ISettleOrderService settleOrderService;


    public ApiMerchantOrderController(IOrderService payOrderService, IExtractService extractService, ISettleOrderService settleOrderService) {
        this.payOrderService = payOrderService;
        this.extractService = extractService;
        this.settleOrderService = settleOrderService;
    }

    @GetMapping("/orders")
    public IPage<OrderDTO> orderList(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        Merchant merchant = theUser();
        return payOrderService.findPageByMchId(new Page<>(current,size),merchant.getId());
    }
    @PostMapping("/query/orders")
    public IPage<OrderDTO> qureyOrderList(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            MchOrderQueryDTO orderQueryDTO){
        Merchant merchant = theUser();
        return payOrderService.findPageByQuery(new Page<>(current,size),merchant.getId(),orderQueryDTO);
    }
    @GetMapping("/extracts")
    public IPage<SettleOrderDTO> extractList(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        Merchant merchant = theUser();
        return settleOrderService.findPageByMchId(new Page<>(current,size),merchant.getId());
    }
    @PostMapping("/query/extracts")
    public IPage<SettleOrderDTO> queryExtractList(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            ExtractQueryDTO queryDTO){
        Merchant merchant = theUser();
        return settleOrderService.findPageByQuery(new Page<>(current,size),merchant.getId(),queryDTO);
    }

    @PostMapping("/payextract")
    public Map payExtract(@Validated SettleOrderInputDTO settleOrderInputDTO) throws ResourceNotFoundException, PostResourceException {
        Map m = new HashMap();
        try {
            settleOrderService.postOrder(settleOrderInputDTO);
        } catch (PostResourceException e) {
            m.put("code",0);
            m.put("msg",e.getMessage());
            return m;
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            m.put("code",0);
            m.put("msg",e.getMessage());
            return m;
        }
        m.put("code",1);
        m.put("msg","提交成功");
        return m;
    }


}
