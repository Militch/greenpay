package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderInputVO;
import com.esiran.greenpay.agentpay.mapper.AgentPayOrderMapper;
import com.esiran.greenpay.agentpay.plugin.AgentPayOrderFlow;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.esiran.greenpay.pay.service.IMerchantPrepaidAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


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
        if (!StringUtils.isEmpty(agentPayOrderDTO.getOrderNo())) {
            wrapper.eq(AgentPayOrder::getOrderNo,agentPayOrderDTO.getOrderNo());
        }
        if (!StringUtils.isEmpty(agentPayOrderDTO.getOutOrderNo())) {
            wrapper.eq(AgentPayOrder::getOutOrderNo,agentPayOrderDTO.getOutOrderNo());
        }
        if (!StringUtils.isEmpty(agentPayOrderDTO.getBatchNo())) {
            wrapper.eq(AgentPayOrder::getBatchNo,agentPayOrderDTO.getBatchNo());
        }
        Page<AgentPayOrder> orderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return orderPage.convert(AgentPayOrderDTO::convertOrderEntity);
    }

    @Override
    public List<AgentPayOrderDTO> agentPayOrderList(Page<AgentPayOrderDTO> page , AgentPayOrderInputVO agentPayOrderInputVO) {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        if (agentPayOrderInputVO != null) {
            if (!StringUtils.isEmpty(agentPayOrderInputVO.getMchId()) && agentPayOrderInputVO.getMchId()>0){
                wrapper.eq(AgentPayOrder::getMchId, agentPayOrderInputVO.getMchId());
            }
            if (!StringUtils.isEmpty(agentPayOrderInputVO.getOrderNo())) {
                wrapper.eq(AgentPayOrder::getOrderNo, agentPayOrderInputVO.getOrderNo());
            }
            if (agentPayOrderInputVO.getStatus() != null && agentPayOrderInputVO.getStatus() != 0) {
                wrapper.eq(AgentPayOrder::getStatus, agentPayOrderInputVO.getStatus());
            }
            if (!StringUtils.isEmpty(agentPayOrderInputVO.getStartTime() )) {
                wrapper.ge(AgentPayOrder::getCreatedAt, agentPayOrderInputVO.getStartTime());
            }

            if (!StringUtils.isEmpty(agentPayOrderInputVO.getEndTime() )) {
                wrapper.lt(AgentPayOrder::getCreatedAt, agentPayOrderInputVO.getEndTime());
            }
        }
        List<AgentPayOrder> agentPayOrder = this.baseMapper.agentPayOrderList(wrapper,((page.getCurrent()-1) * page.getSize()),page.getSize());
        List<AgentPayOrderDTO> agentPayOrderDTOStream = agentPayOrder.stream().map(AgentPayOrderDTO::convertOrderEntity).collect(Collectors.toList());

        return agentPayOrderDTOStream;
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
    @Transactional
    public String createOneBatchOrder(AgentPayOrder agentPayOrder) throws APIException {
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
            Map<String, Object> results = agentPayOrderFlow.getResults();
            return (String) results.get("status");
        } catch (Exception e) {
            if (e instanceof APIException)
                throw new APIException(e.getMessage(),((APIException) e).getCode(),((APIException) e).getStatus());
            if (!StringUtils.isEmpty(e.getMessage())){
                throw new APIException(e.getMessage(),"CALL_AGENT_PAY_PASSAGE_ERROR",500);
            }
            throw new APIException("系统错误，调用代付通道接口执行失败","CALL_AGENT_PAY_PASSAGE_ERROR",500);
        }
    }



    @Override
    public List<AgentPayOrderDTO> findIntradayOrdersByMchId(Integer mchId) {
        List<AgentPayOrderDTO> intradayOrders = this.baseMapper.findIntradayOrdersByMchId(mchId);
        return intradayOrders;
    }

    @Override
    public List<AgentPayOrderDTO> findYesterdayOrdersByMchId(Integer mchId) {
        List<AgentPayOrderDTO> yesterdayOrders = this.baseMapper.findYesterdayOrdersByMchId(mchId);
        return yesterdayOrders;
    }

    @Override
    public List<AgentPayOrderDTO> findIntradayOrders() {
        List<AgentPayOrderDTO> intradayOrders = this.baseMapper.findIntradayOrders();
        return intradayOrders;
    }

    @Override
    public List<AgentPayOrderDTO> findYesterdayOrders() {
        List<AgentPayOrderDTO> yesterdayOrders = this.baseMapper.findYesterdayOrders();
        return yesterdayOrders;
    }


    @Override
    public List<CartogramDTO> hourAllData() {
        return this.baseMapper.hourAllData();
    }

    @Override
    public List<CartogramDTO> sevenDayAllData() {
        return this.baseMapper.sevenDayAllData();
    }

    @Override
    public List<CartogramDTO> upWeekAllData() {
        return this.baseMapper.upWeekAllData();
    }

    @Override
    public List<CartogramDTO> sevenDay4CountAndAmount() {
        return this.baseMapper.sevenDay4CountAndAmount();
    }

    @Override
    public List<CartogramDTO> currentMonth4CountAndAmount() {
        return this.baseMapper.currentMonth4CountAndAmount();
    }

    @Override
    public List<CartogramPayDTO> payRanking() {
        return this.baseMapper.payRanking();
    }

    @Override
    public List<CartogramPayStatusVo> payCRV() {
        return this.baseMapper.payCRV();
    }

}
