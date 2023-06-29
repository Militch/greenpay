package com.esiran.greenpay.admin.controller.settle;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.esiran.greenpay.pay.service.IOrderService;
import com.esiran.greenpay.settle.entity.SettleOrderDTO;
import com.esiran.greenpay.settle.entity.SettleOrderQueryDto;
import com.esiran.greenpay.settle.service.ISettleOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settle")
public class APIAdminSettleOrderController {
    private ISettleOrderService orderService;
    public APIAdminSettleOrderController(ISettleOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/audits")
    public IPage<SettleOrderDTO> audit(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            SettleOrderQueryDto settleOrderQueryDto){
        return orderService.selectPageByAudit(new Page<>(current,size), settleOrderQueryDto);
    }
    @GetMapping("/payable")
    public IPage<SettleOrderDTO> payable(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size ,
            SettleOrderQueryDto settleOrderQueryDto){
        return orderService.selectPageByPayable(new Page<>(current,size), settleOrderQueryDto);
    }
}
