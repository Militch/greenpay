package com.esiran.greenpay.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.mapper.PassageAccountMapper;
import com.esiran.greenpay.pay.service.IPassageAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.pay.service.IPassageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 支付通道账户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class PassageAccountServiceImpl extends ServiceImpl<PassageAccountMapper, PassageAccount> implements IPassageAccountService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private final IPassageService passageService;

    private static final Gson gson = new GsonBuilder().create();
    public PassageAccountServiceImpl(IPassageService passageService) {
        this.passageService = passageService;
    }

    @Override
    public List<PassageAccount> listAvailable(Integer passageId) {
        LambdaQueryWrapper<PassageAccount> paQueryWrapper
                = new LambdaQueryWrapper<>();
        paQueryWrapper.eq(PassageAccount::getPassageId,passageId)
                .eq(PassageAccount::getStatus,1)
                .gt(PassageAccount::getWeight,0)
                .orderByDesc(PassageAccount::getWeight);
        return list(paQueryWrapper);
    }

    @Override
    public List<PassageAccount> listByPayTypeCode(String payTypeCode) {
        LambdaQueryWrapper<PassageAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PassageAccount::getPayTypeCode,payTypeCode);
        return this.list(queryWrapper);
    }

    @Override
    public PassageAccountDTO getDTOById(Integer id) {
        PassageAccount pa = getById(id);
        PassageAccountDTO data = modelMapper.map(pa, PassageAccountDTO.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        data.setInterfaceAttrDisplay(pa.getInterfaceAttr());
        return data;
    }

    @Override
    public IPage<PassageAccountDTO> selectPage(IPage<PassageAccountDTO> page, PassageAccountDTO passageAccountDTO) {
        PassageAccount passageAccount = modelMapper.map(passageAccountDTO,PassageAccount.class);
        LambdaQueryWrapper<PassageAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(passageAccount);
        IPage<PassageAccount> passageAccountPage = this.page(new Page<>(page.getCurrent(),page.getSize()),lambdaQueryWrapper);
        return passageAccountPage.convert(item->{
            PassageAccountDTO data = modelMapper.map(item, PassageAccountDTO.class);
            Passage passage = passageService.getById(item.getPassageId());
            data.setPassageName(passage.getPassageName());
            return data;
        });
    }

    @Override
    public int add(PassageAccountInputDTO passageAccountInputDTO) throws PostResourceException {
        PassageAccount target = modelMapper.map(passageAccountInputDTO,PassageAccount.class);
        Passage passage = passageService.getById(target.getPassageId());
        if (passage == null) throw new PostResourceException("支付通道不存在");
        String attrJson = passageAccountInputDTO.getInterfaceAttr();
        target.setInterfaceAttr(checkInterfaceAttrJson(attrJson));
        target.setPayTypeCode(passage.getPayTypeCode());
        save(target);
        return target.getId();
    }

    private static String checkInterfaceAttrJson(String attrJson) throws PostResourceException {
        if (attrJson == null) return null;
        Map<String,Object> attr;
        try {
            attr = gson.fromJson(attrJson,
                    new TypeToken<Map<String,Object>>(){}.getType());
        }catch (Exception e){
            throw new PostResourceException("接口参数格式不正确");
        }
        if (attr == null)
            throw new PostResourceException("接口参数格式不正确");
        return gson.toJson(attr);
    }
    @Override
    public boolean updateById(Integer id, PassageAccountInputDTO passageAccountInputDTO) throws PostResourceException, ResourceNotFoundException {
        PassageAccount src = this.getById(id);
        if (src == null) throw new ResourceNotFoundException("支付通道不存在");
        PassageAccount passageAccount = modelMapper.map(passageAccountInputDTO,PassageAccount.class);
        passageAccount.setId(id);
        Passage passage = passageService.getById(passageAccount.getPassageId());
        if (passage == null) throw new PostResourceException("支付通道不存在");
        String attrJson = passageAccountInputDTO.getInterfaceAttr();
        passageAccount.setInterfaceAttr(checkInterfaceAttrJson(attrJson));
        return updateById(passageAccount);
    }

    @Override
    public void delIds(List<Integer> ids) {
        for (Integer id : ids){
            this.removeById(id);
        }
    }
}
