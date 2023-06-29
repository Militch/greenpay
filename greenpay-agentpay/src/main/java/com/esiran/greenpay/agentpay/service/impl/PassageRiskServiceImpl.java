package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.entity.PassageRisk;
import com.esiran.greenpay.agentpay.entity.PassageRiskInputDTO;
import com.esiran.greenpay.agentpay.mapper.PassageRiskMapper;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.agentpay.service.IPassageRiskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.NumberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-06-05
 */
@Service
public class PassageRiskServiceImpl extends ServiceImpl<PassageRiskMapper, PassageRisk> implements IPassageRiskService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private final IAgentPayPassageService agentPayPassageService;

    public PassageRiskServiceImpl(IAgentPayPassageService agentPayPassageService) {
        this.agentPayPassageService = agentPayPassageService;
    }

    @Override
    public PassageRisk getByPassageId(Integer passageId) {
        LambdaQueryWrapper<PassageRisk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PassageRisk::getPassageId,passageId);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public void updateRisk(Integer passageId, PassageRiskInputDTO inputDTO) throws ResourceNotFoundException, PostResourceException {
        BigDecimal b = new BigDecimal (0);
        BigDecimal amountMin = new BigDecimal(inputDTO.getAmountMinDisplay());
        inputDTO.setAmountMin(NumberUtil.amountYuan2fen(amountMin));
        BigDecimal amountMax = new BigDecimal(inputDTO.getAmountMaxDisplay());
        inputDTO.setAmountMax(NumberUtil.amountYuan2fen(amountMax));
        if (amountMin.compareTo(b) == -1 || amountMax.compareTo(b) == -1){
            throw new PostResourceException("最低金额不能小于0");
        }
        AgentPayPassage passage = agentPayPassageService.getById(passageId);
        if (passage == null){
            throw new PostResourceException("代付通道不存在");
        }
        LambdaQueryWrapper<PassageRisk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PassageRisk::getPassageId,passageId);
        PassageRisk passageRisk = this.baseMapper.selectOne(wrapper);
        if (passageRisk == null){
            try {
                PassageRisk risk = modelMapper.map(inputDTO, PassageRisk.class);
                risk.setPassageId(passageId);
                risk.setPassageName(passage.getPassageName());
                risk.setCreatedAt(LocalDateTime.now());
                risk.setUpdatedAt(LocalDateTime.now());
                this.baseMapper.insert(risk);
            } catch (Exception e) {
                throw new PostResourceException("系统异常");
            }
        }else {
            try {
                PassageRisk risk = modelMapper.map(inputDTO, PassageRisk.class);
                risk.setPassageId(passageId);
                risk.setPassageName(passage.getPassageName());
                risk.setUpdatedAt(LocalDateTime.now());
                LambdaUpdateWrapper<PassageRisk> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PassageRisk::getPassageId,passageId);
                this.baseMapper.update(risk,updateWrapper);
            } catch (Exception e) {
                throw new PostResourceException("系统异常");
            }
        }
    }
}
