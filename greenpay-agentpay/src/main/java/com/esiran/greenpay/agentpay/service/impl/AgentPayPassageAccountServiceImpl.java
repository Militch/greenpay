package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.agentpay.entity.*;
import com.esiran.greenpay.agentpay.mapper.AgentPayPassageAccountMapper;
import com.esiran.greenpay.agentpay.plugin.AgentPayOrderFlow;
import com.esiran.greenpay.agentpay.plugin.AgentPayQueryFlow;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.service.IInterfaceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 代付通道账户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class AgentPayPassageAccountServiceImpl extends ServiceImpl<AgentPayPassageAccountMapper, AgentPayPassageAccount> implements IAgentPayPassageAccountService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private final IAgentPayPassageService passageService;
    private final IInterfaceService interfaceService;
    private final PluginLoader pluginLoader;
    public AgentPayPassageAccountServiceImpl(IAgentPayPassageService passageService, IInterfaceService interfaceService, PluginLoader pluginLoader) {
        this.passageService = passageService;
        this.interfaceService = interfaceService;
        this.pluginLoader = pluginLoader;
    }

    @Override
    public List<AgentPayPassageAccount> listByPayTypeCode(String payTypeCode) {
        LambdaQueryWrapper<AgentPayPassageAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentPayPassageAccount::getPayTypeCode,payTypeCode);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<AgentPayPassageAccountDTO> selectPage(IPage<AgentPayPassageAccountDTO> page, AgentPayPassageAccountDTO passageAccountDTO) {
        AgentPayPassageAccount passageAccount = modelMapper.map(passageAccountDTO, AgentPayPassageAccount.class);
        LambdaQueryWrapper<AgentPayPassageAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(passageAccount);
        IPage<AgentPayPassageAccount> passageAccountPage = this.page(new Page<>(page.getCurrent(),page.getSize()),lambdaQueryWrapper);
        return passageAccountPage.convert(item->{
            AgentPayPassageAccountDTO data = modelMapper.map(item, AgentPayPassageAccountDTO.class);
            AgentPayPassage passage = passageService.getById(item.getPassageId());
            data.setPassageName(passage.getPassageName());
            return data;
        });
    }

    @Override
    public int add(AgentPayPassageAccountInputDTO passageAccountInputDTO) throws PostResourceException {
        AgentPayPassageAccount target = modelMapper.map(passageAccountInputDTO, AgentPayPassageAccount.class);
        AgentPayPassage passage = passageService.getById(target.getPassageId());
        if (passage == null) throw new PostResourceException("支付通道不存在");
        target.setPayTypeCode(passage.getPayTypeCode());
        save(target);
        return target.getId();
    }


    @Override
    public boolean updateById(Integer id, AgentPayPassageAccountInputDTO passageAccountInputDTO) throws PostResourceException, ResourceNotFoundException {
        AgentPayPassageAccount src = this.getById(id);
        if (src == null) throw new ResourceNotFoundException("支付通道不存在");
        AgentPayPassageAccount passageAccount = modelMapper.map(passageAccountInputDTO, AgentPayPassageAccount.class);
        passageAccount.setId(id);
        AgentPayPassage passage = passageService.getById(passageAccount.getPassageId());
        if (passage == null) throw new PostResourceException("支付通道不存在");
        return updateById(passageAccount);
    }

    @Override
    @Transactional
    public void delByIds(List<Integer> ids) throws PostResourceException {
        for (Integer id : ids){
            this.removeById(id);
        }
    }

    @Override
    public List<AgentPayPassageAccount> listAvailableByPassageId(Integer passageId) {
        LambdaQueryWrapper<AgentPayPassageAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentPayPassageAccount::getPassageId, passageId)
                .eq(AgentPayPassageAccount::getStatus, 1)
                .gt(AgentPayPassageAccount::getWeight, 0);
        return this.list(queryWrapper);
    }

    @Override
    public List<AgentPayPassageAccount> filterAppa(List<AgentPayPassageAccount> accounts, Integer orderAmount) {
        orderAmount = orderAmount == null?0:orderAmount;
        Integer finalOrderAmount = orderAmount;
        List<AgentPayPassageAccount> a = accounts.stream().peek(item->{
            Integer appaBalance = getAppaBalance(item);
            appaBalance = appaBalance == null ? 0 : appaBalance;
            int diff = appaBalance - finalOrderAmount;
            Integer w = item.getWeight();
            w += diff;
            item.setWeight(w);
        }).collect(Collectors.toList());
        a = a.stream().filter(item-> item.getWeight()>0).collect(Collectors.toList());
        a = a.stream().sorted(Comparator.comparing(AgentPayPassageAccount::getWeight)).collect(Collectors.toList());
        return a;
    }

    @Override
    public Integer getAppaBalance(AgentPayPassageAccount appa) {
        if (appa == null) return null;
        AgentPayPassage passage =  passageService.getById(appa.getPassageId());
        if (passage == null) return null;
        Interface ins = interfaceService.getByCode(passage.getInterfaceCode());

        AgentPayQueryFlow agentPayQueryFlow = new AgentPayQueryFlow(appa);
        try {
            String impl = ins.getInterfaceImpl();
            Pattern pattern = Pattern.compile("query:(.+?)(;|$)",Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(impl);
            if (!m.matches()) return null;
            Plugin<AgentPayPassageAccount> plugin = pluginLoader.loadForClassPath(m.group(1));
            plugin.apply(agentPayQueryFlow);
            agentPayQueryFlow.execDependent("queryBalance");
            Map<String,Object> result = agentPayQueryFlow.getResults();
            if (result == null) return null;
            return (Integer) result.get("balance");
        } catch (Exception e) {
            return null;
        }
    }


    private static int randomPickIndex(int[] w){
        int len = w.length;
        if (len == 0) return -1;
        if (len == 1) return 0;
        int bound = w[len-1];
        Random random = new Random(System.currentTimeMillis());
        int val = random.nextInt(bound)+1;
        int left = 0, right = len-1, mid;
        while (left < right){
            mid = (right - left) / 2 + left;
            if (w[mid] == val){
                return mid;
            }else if(w[mid] > val){
                right = mid;
            }else {
                left = mid + 1;
            }
        }
        return left;
    }
    @Override
    public AgentPayPassageAccount schedulerAgentPayPassageAcc(Integer passageId) {
        return schedulerAgentPayPassageAcc(passageId,0);
    }

    @Override
    public AgentPayPassageAccount schedulerAgentPayPassageAcc(Integer passageId, Integer orderAmount) {
        List<AgentPayPassageAccount> passageAccounts = listAvailableByPassageId(passageId);
        if (passageAccounts == null || passageAccounts.size() == 0) return null;
        passageAccounts = filterAppa(passageAccounts,orderAmount);
        // 构造权重区间值数组
        int[] sumArr = new int[passageAccounts.size()];
        // 权重总和
        int sum = 0;
        for (int i=0; i<sumArr.length; i++){
            AgentPayPassageAccount account = passageAccounts.get(i);
            int w = account.getWeight();
            sum += w;
            sumArr[i] = sum;
        }
        // 根据权重随机获取数组索引
        int index = randomPickIndex(sumArr);
        AgentPayPassageAccount account = passageAccounts.get(index);
        if (account == null || !account.getStatus()) return null;
        return account;
    }
}
