package com.esiran.greenpay.agentpay.plugin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.bank.pingan.api.PingAnApiEx;
import com.esiran.greenpay.bank.pingan.entity.HeaderMsg;
import com.esiran.greenpay.bank.pingan.entity.OnceAgentPay;
import com.esiran.greenpay.bank.pingan.entity.QueryOnceAgentPay;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class PingAnPlugin implements Plugin<AgentPayOrder> {
    private static final Gson g = new Gson();
    private static IAgentPayOrderService agentPayOrderService;
    private PingAnPlugin(IAgentPayOrderService agentPayOrderService){
        this.agentPayOrderService = agentPayOrderService;
    }
    private static final class OrderCreateTask implements Task<AgentPayOrder> {

        @Override
        public String taskName() {
            return "PinAnCreate";
        }

        @Override
        public String dependent() {
            return "create";
        }

        @Override
        public void action(Flow<AgentPayOrder> flow) throws Exception {
//            AgentPayOrder data = flow.getData();
//            LambdaUpdateWrapper<AgentPayOrder> wrapper = new LambdaUpdateWrapper<>();
//            wrapper.set(AgentPayOrder::getStatus,2)
//                    .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
//                    .eq(AgentPayOrder::getId,data.getId());
//            agentPayOrderService.update(wrapper);
//            Map<String,Object> returns = new HashMap<>();
//            returns.put("status","40");
//            flow.returns(returns);
            AgentPayOrder data = flow.getData();
            if (data.getStatus() != 1){
                throw new APIException("代付订单状态异常","");
            }
            String attr = data.getPayInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(attr);
            if (attrmap == null
                    || attrmap.get("companyCode") == null
                    || attrmap.get("acctNo") == null
                    || attrmap.get("host") == null){
                throw new APIException("代付接口参数异常","");
            }
            HeaderMsg headerMsg = new HeaderMsg();
            headerMsg.setOutOrderNumber(data.getOrderSn());
            headerMsg.setCompanyCode(attrmap.get("companyCode"));
            PingAnApiEx apiEx = new PingAnApiEx(attrmap.get("host"),headerMsg);
            Map<String, String> queryAmount = apiEx.queryAmount(attrmap.get("acctNo"));
            if (queryAmount == null){
                throw new APIException("代付渠道请求失败","");
            }
            String balance = queryAmount.get("Balance");
            Integer blanceFen = NumberUtil.amountYuan2fen(new BigDecimal(balance));
            if (data.getAmount() > blanceFen){
                throw new APIException("代付余额不足","");
            }
            OnceAgentPay onceAgentPay = new OnceAgentPay();
            onceAgentPay.setOrderNumber(data.getOrderNo());
            onceAgentPay.setTranAmount(NumberUtil.amountFen2Yuan(data.getAmount()));
            onceAgentPay.setAcctNo(attrmap.get("acctNo"));
            onceAgentPay.setInAcctNo(data.getAccountNumber());
            onceAgentPay.setInAcctName(data.getAccountName());
            Map<String, String> onceMap = apiEx.onceAgentPay(onceAgentPay);
//            if (onceMap != null && onceMap.get("status").equals("-1")){
//                throw new APIException(onceMap.get("msg"),"代付渠道请求失败");
//            }
            if (onceMap != null && StringUtils.isEmpty(onceMap.get("status"))){
                LambdaUpdateWrapper<AgentPayOrder> wrapper = new LambdaUpdateWrapper<>();
                wrapper.set(AgentPayOrder::getStatus,2)
                        .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                        .eq(AgentPayOrder::getId,data.getId());
                agentPayOrderService.update(wrapper);
                QueryOnceAgentPay queryOnceAgentPay = new QueryOnceAgentPay();
                queryOnceAgentPay.setOrderNumber(data.getOrderNo());
                queryOnceAgentPay.setAcctNo(attrmap.get("acctNo"));
                Map<String, String> map = apiEx.queryOnceAgentPay(queryOnceAgentPay);
                Map<String,Object> returns = new HashMap<>();
                if (map != null){
                    if (map.get("Status").equals("30")){
                        returns.put("status","30");
                        flow.returns(returns);
                    }
                    if (map.get("Status").equals("20")){
                        returns.put("status","20");
                        flow.returns(returns);
                    }
                    if (!(map.get("Status").equals("30") || map.get("Status").equals("20"))){
                        returns.put("status","40");
                        flow.returns(returns);
                    }
                }else {
                    throw new APIException("代付渠道请求失败","");
                }
            }else {
                throw new APIException("代付渠道请求失败","");
            }
        }
    }



    @Override
    public void apply(Flow<AgentPayOrder> flow) {
        flow.add(new OrderCreateTask());
    }
}
