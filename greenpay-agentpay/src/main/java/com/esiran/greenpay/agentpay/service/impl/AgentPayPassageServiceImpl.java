package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayBatch;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageInputDTO;
import com.esiran.greenpay.agentpay.mapper.AgentPayPassageMapper;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageAccountService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.entity.PassageAccount;
import com.esiran.greenpay.pay.entity.ProductPassage;
import com.esiran.greenpay.pay.entity.TypeDTO;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.esiran.greenpay.pay.service.ITypeService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 代付通道 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class AgentPayPassageServiceImpl extends ServiceImpl<AgentPayPassageMapper, AgentPayPassage> implements IAgentPayPassageService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private final ITypeService typeService;
    private final IInterfaceService interfaceService;
    private final IAgentPayPassageAccountService agentPayPassageAccountService;
    public AgentPayPassageServiceImpl(
            ITypeService typeService,
            IInterfaceService interfaceService,
            @Lazy IAgentPayPassageAccountService agentPayPassageAccountService) {
        this.typeService = typeService;
        this.interfaceService = interfaceService;
        this.agentPayPassageAccountService = agentPayPassageAccountService;
    }

    @Override
    public List<AgentPayPassage> listByPayTypeCode(String payTypeCode) {
        LambdaQueryWrapper<AgentPayPassage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentPayPassage::getPayTypeCode,payTypeCode);
        return list(queryWrapper);
    }

    @Override
    public List<AgentPayPassageDTO> selectPage(IPage<AgentPayPassageDTO> page, AgentPayPassageDTO passageDTO) {
        LambdaQueryWrapper<AgentPayPassage> wrapper = new LambdaQueryWrapper<>();
        if (passageDTO != null) {
            if (!StringUtils.isEmpty(passageDTO.getPassageName())) {
                wrapper.eq(AgentPayPassage::getPassageName,passageDTO.getPassageName());
            }
            if (!StringUtils.isEmpty(passageDTO.getPayTypeCode())) {
                wrapper.eq(AgentPayPassage::getPayTypeCode,passageDTO.getPayTypeCode());
            }
        }

        List<AgentPayPassage> agentPayOrder = this.baseMapper.agentPayPassageList(wrapper,((page.getCurrent()-1) * page.getSize()),page.getSize());
        List<AgentPayPassageDTO> menuTreeVos = agentPayOrder.stream()
                .map(item -> modelMapper.map(item, AgentPayPassageDTO.class))
                .collect(Collectors.toList());
        return menuTreeVos;
    }


    @Override
    public int add(AgentPayPassageInputDTO passageInputDTO) throws PostResourceException {
        AgentPayPassage target = modelMapper.map(passageInputDTO, AgentPayPassage.class);
        TypeDTO typeDTO = typeService.getTypeByCode(target.getPayTypeCode());
        checkupPost(target, typeDTO);
        save(target);
        return target.getId();
    }

    @Override
    public boolean updateById(Integer id, AgentPayPassageInputDTO passageInputDTO) throws PostResourceException, ResourceNotFoundException {
        AgentPayPassage src = this.getById(id);
        if (src == null) throw new ResourceNotFoundException("支付通道不存在");
        AgentPayPassage passage = modelMapper.map(passageInputDTO, AgentPayPassage.class);
        passage.setId(id);
        TypeDTO typeDTO = typeService.getTypeByCode(passage.getPayTypeCode());
        checkupPost(passage, typeDTO);
        return updateById(passage);
    }

    @Override
    public void delIds(List<Integer> ids) throws PostResourceException {
        for (Integer id : ids){
            LambdaQueryWrapper<AgentPayPassageAccount> agentPayPassageAccountQueryWrapper
                    = new LambdaQueryWrapper<>();
            agentPayPassageAccountQueryWrapper.eq(AgentPayPassageAccount::getPassageId,id);
            List<AgentPayPassageAccount> apas = agentPayPassageAccountService
                    .list(agentPayPassageAccountQueryWrapper);
            if (apas == null || apas.size() > 0){
                throw new PostResourceException("代付通道还有关联的子账户，无法删除");
            }
            this.removeById(id);
        }
    }

    private void checkupPost(AgentPayPassage passage, TypeDTO typeDTO) throws PostResourceException {
        if (typeDTO == null) throw new PostResourceException("支付类型不存在");
        Interface ins = interfaceService.getByCode(passage.getInterfaceCode());
        if (ins == null) throw new PostResourceException("支付接口不存在");
        if (!ins.getPayTypeCode().equals(typeDTO.getTypeCode())){
            throw new PostResourceException("支付类型与支付接口不匹配");
        }
    }
}
