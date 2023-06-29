package com.esiran.greenpay.admin.controller.pay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.InterfaceQueryDTO;
import com.esiran.greenpay.pay.entity.PassageAccountDTO;
import com.esiran.greenpay.pay.entity.PassageDTO;
import com.esiran.greenpay.pay.entity.PassageQueryDTO;
import com.esiran.greenpay.pay.service.IPassageAccountService;
import com.esiran.greenpay.pay.service.IPassageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay/passages")
public class APIAdminPayPassageController {
    private IPassageService passageService;
    private IPassageAccountService passageAccountService;
    public APIAdminPayPassageController(
            IPassageService passageService,
            IPassageAccountService passageAccountService) {
        this.passageService = passageService;
        this.passageAccountService = passageAccountService;
    }


    @GetMapping
    public IPage<PassageDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size, PassageQueryDTO passageQueryDTO){
        return passageService.selectPage(new Page<>(current,size),passageQueryDTO);
    }
    @GetMapping("/{passageId}/accounts")
    public IPage<PassageAccountDTO> listAcc(
            @PathVariable Integer passageId,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        PassageAccountDTO passageAccountDTO = new PassageAccountDTO();
        passageAccountDTO.setPassageId(passageId);
        return passageAccountService.selectPage(new Page<>(current,size),passageAccountDTO);
    }
}
