package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.mapper.AgentPayOrderMapper;
import com.esiran.greenpay.agentpay.plugin.AgentPayOrderFlow;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.service.IInterfaceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 代付订单表 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class AgentPayOrderServiceImpl extends ServiceImpl<AgentPayOrderMapper, AgentPayOrder> implements IAgentPayOrderService {

    private IAgentPayPassageService agentPayPassageService;
    private IInterfaceService interfaceService;
    private PluginLoader pluginLoader;

    public AgentPayOrderServiceImpl(IAgentPayPassageService agentPayPassageService, IInterfaceService interfaceService, PluginLoader pluginLoader) {
        this.agentPayPassageService = agentPayPassageService;
        this.interfaceService = interfaceService;
        this.pluginLoader = pluginLoader;
    }

    @Override
    public IPage<AgentPayOrderDTO> selectPage(Page<AgentPayOrderDTO> page, AgentPayOrderDTO agentPayOrderDTO) {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AgentPayOrder::getCreatedAt);
        Page<AgentPayOrder> orderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return orderPage.convert(AgentPayOrderDTO::convertOrderEntity);
    }

    @Override
    public AgentPayOrderDTO getbyOrderNo(String orderNo) {
        AgentPayOrder oneByOrderNo = this.getOneByOrderNo(orderNo);
        if (oneByOrderNo == null ) return null;
        return AgentPayOrderDTO.convertOrderEntity(oneByOrderNo);
    }

    @Override
    public AgentPayOrder getOneByOrderNo(String orderNo) {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentPayOrder::getOrderNo,orderNo);
        return this.getOne(wrapper);
    }

    @Override
    public void createOneBatchOrder(AgentPayOrder agentPayOrder) {
        agentPayOrder.setStatus(1);
        agentPayOrder.setCreatedAt(LocalDateTime.now());
        agentPayOrder.setUpdatedAt(LocalDateTime.now());
        this.save(agentPayOrder);
        AgentPayPassage payPassage = agentPayPassageService.getById(agentPayOrder.getAgentpayPassageId());
        Interface ints = interfaceService.getByCode(payPassage.getInterfaceCode());
        AgentPayOrderFlow agentPayOrderFlow = new AgentPayOrderFlow(agentPayOrder);
        try {

            String impl = ints.getInterfaceImpl();
            Pattern pattern = Pattern.compile("(^|;)corder:(.+?)(;|$)");
            Matcher m = pattern.matcher(impl);
            if (!m.find())
                throw new IllegalAccessException("接口调用失败");
            Plugin<AgentPayOrder> plugin = pluginLoader.loadForClassPath(m.group(2));
            plugin.apply(agentPayOrderFlow);
            agentPayOrderFlow.execDependent("create");
        } catch (Exception e) {
            if (e instanceof APIException)
                System.out.println(e.getMessage());
            if (!StringUtils.isEmpty(e.getMessage())){
                System.out.println(e.getMessage());
            }
        }
    }
}
