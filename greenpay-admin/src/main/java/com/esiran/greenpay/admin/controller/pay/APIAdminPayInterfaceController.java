package com.esiran.greenpay.admin.controller.pay;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.InterfaceDTO;
import com.esiran.greenpay.pay.entity.InterfaceQueryDTO;
import com.esiran.greenpay.pay.entity.Product;
import com.esiran.greenpay.pay.entity.Type;
import com.esiran.greenpay.pay.entity.TypeDTO;
import com.esiran.greenpay.pay.service.IInterfaceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pay/interfaces")
public class APIAdminPayInterfaceController {
    private IInterfaceService interfaceService;

    public APIAdminPayInterfaceController(IInterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @GetMapping
    public IPage<InterfaceDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size, InterfaceQueryDTO interfaceQueryDTO){
        return interfaceService.selectPage(new Page<>(current,size),interfaceQueryDTO);
    }

}
