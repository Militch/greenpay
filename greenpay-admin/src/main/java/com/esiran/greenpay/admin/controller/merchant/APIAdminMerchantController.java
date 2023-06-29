package com.esiran.greenpay.admin.controller.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.RSAUtil;
import com.esiran.greenpay.merchant.entity.*;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/merchants")
public class APIAdminMerchantController extends CURDBaseController {
    private final IMerchantService merchantService;
    private final IAgentPayOrderService agentPayOrderService;
    private final IUserService userService;
    private final IPrepaidAccountService prepaidAccountService;
    private final IApiConfigService apiConfigService;
    public APIAdminMerchantController(IMerchantService merchantService, IAgentPayOrderService agentPayOrderService, IUserService userService, IPrepaidAccountService prepaidAccountService, IApiConfigService apiConfigService) {
        this.merchantService = merchantService;
        this.agentPayOrderService = agentPayOrderService;
        this.userService = userService;
        this.prepaidAccountService = prepaidAccountService;
        this.apiConfigService = apiConfigService;
    }

    @GetMapping
    public IPage<MerchantDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        return merchantService.selectMerchantByPage(new Page<>(current,size));
    }


    @GetMapping("/{mchId}/products")
    public List<MerchantProductDTO> product(@PathVariable Integer mchId) throws APIException, ResourceNotFoundException {
        return merchantService.selectMchProductById(mchId);
    }


    @GetMapping("/{mchId}/agent_pay_passages")
    public List<MerchantAgentPayPassageDTO> agentPayPassage(@PathVariable Integer mchId) throws APIException, ResourceNotFoundException {
        Merchant mch = merchantService.getById(mchId);
        if (mch == null) throw new ResourceNotFoundException("商户不存在");
        return merchantService.listMchAgentPayPassageByMchId(mchId);
    }


    @PostMapping(value = "/{mchId}/products")
    public void updateProduct(@PathVariable String mchId, @Valid MerchantProductInputDTO dto) throws Exception {
        merchantService.updateMerchantProduct(dto,Integer.valueOf(mchId));
    }

    @PostMapping(value = "/{mchId}")
    public void updateUserInfo(@PathVariable String mchId, @Valid MerchantUpdateDTO merchantDTO) throws Exception {
        merchantService.updateMerchantInfoById(merchantDTO,Integer.valueOf(mchId));
    }

    @PostMapping(value = "/{mchId}/security")
    public void updateSecurity(@PathVariable String mchId, @RequestParam String password) throws Exception {
        merchantService.updatePasswordById(password,Integer.valueOf(mchId));
    }


    @PostMapping(value = "/{mchId}/settle")
    public void updateSettleInfo(@PathVariable String mchId, SettleAccountDTO settleAccountDTO) throws Exception {
        merchantService.updateSettleById(settleAccountDTO,Integer.valueOf(mchId));
    }

    @PostMapping(value = "/{mchId}/pay/account")
    public void payAccount(@PathVariable String mchId,
                           @RequestParam Integer action,
                           @RequestParam Integer type,
                           @RequestParam Double amount) throws Exception {
        merchantService.updateAccountBalance(1,Integer.valueOf(mchId),amount,type,action);
    }
    @PostMapping(value = "/{mchId}/prepaid/account")
    public void prepaidAccount(@PathVariable String mchId,
                           @RequestParam Integer action,
                           @RequestParam Integer type,
                           @RequestParam Double amount) throws Exception {
        merchantService.updateAccountBalance(2,Integer.valueOf(mchId),amount,type,action);
    }


    @PostMapping(value = "/{mchId}/mch_pub_key",produces = "text/plain")
    public void publicKey(@PathVariable String mchId, @RequestBody String content) throws Exception {
        LambdaQueryWrapper<ApiConfig> queryWrapper = new QueryWrapper<ApiConfig>()
                .lambda().eq(ApiConfig::getMchId,mchId);
        ApiConfig apiConfig = apiConfigService.getOne(queryWrapper);
        if (apiConfig == null) throw new Exception("商户不存在");
        String publicKey = RSAUtil.resolvePublicKey(content);

        LambdaUpdateWrapper<ApiConfig> updateWrapper = new UpdateWrapper<ApiConfig>().lambda();
        updateWrapper.set(ApiConfig::getMchPubKey,publicKey)
                .eq(ApiConfig::getMchId,mchId);
        apiConfigService.update(updateWrapper);
    }

    /**
     * 代付退款
     * @param orderNo
     * @param supplyPass
     * @throws Exception
     */
    @GetMapping("/refund")
    public void refund(@RequestParam String orderNo
            ,@RequestParam String supplyPass) throws Exception {
        User user = theUser();
        try {
            boolean result = userService.verifyTOTPPass(user.getId(),supplyPass);
            if (!result) {
                throw new IllegalArgumentException("动态密码校验失败");
            }
        }catch (Exception e){
            throw new PostResourceException(e.getMessage());
        }
        AgentPayOrder agentPayOrder = agentPayOrderService.getOneByOrderNo(orderNo);
        if (agentPayOrder == null){
            throw new Exception("订单存在");
        }
        if (agentPayOrder.getStatus() != -1){
            throw new Exception("该订单不支持退款");
        }
        prepaidAccountService.updateBalance(agentPayOrder.getMchId()
                ,-(agentPayOrder.getAmount()+agentPayOrder.getFee())
                ,0);
    }
}
