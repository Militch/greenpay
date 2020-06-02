package com.esiran.greenpay.admin.controller.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.InterfaceDTO;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.esiran.greenpay.pay.service.IOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class APIAdminPayOrderController {
    private IOrderService orderService;
    public APIAdminPayOrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public IPage<OrderDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            OrderQueryDTO orderQueryDTO){
        return orderService.selectPage(new Page<>(current,size),orderQueryDTO);
    }


}
