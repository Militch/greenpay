package com.esiran.greenpay.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.mapper.TypeMapper;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.esiran.greenpay.pay.service.ITypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.util.TextUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 支付类型 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements ITypeService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private final IInterfaceService interfaceService;

    public TypeServiceImpl(
            @Lazy IInterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @Override
    public Type findTypeByCode(String code) {
        LambdaQueryWrapper<Type> typeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        typeLambdaQueryWrapper.eq(Type::getTypeCode,code);
        return getOne(typeLambdaQueryWrapper);
    }

    @Override
    public TypeDTO getTypeByCode(String code) {
        Type type = findTypeByCode(code);
        if (type == null) return null;
        return modelMapper.map(type,TypeDTO.class);
    }

    @Override
    public List<Type> listByType(Integer type) {
        LambdaQueryWrapper<Type> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Type::getType,type);
        return this.list(queryWrapper);
    }

    @Override
    public List<Type> listByPayType() {
        return this.listByType(1);
    }

    @Override
    public List<Type> listByAgentPayType() {
        return this.listByType(2);
    }

    @Override
    public IPage<Type> selectPageByType(IPage<Type> page, TypeQueryDto queryDto) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Type type = modelMapper.map(queryDto, Type.class);

        LambdaQueryWrapper<Type> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Type::getId);
        lambdaQueryWrapper.setEntity(type);

        IPage<Type> typeIPage = this.page(page, lambdaQueryWrapper);
        return typeIPage;
    }

    @Override
    public void saveType(TypeInputDTO dto) throws PostResourceException {
        Type type = modelMapper.map(dto,Type.class);
        Type query = findTypeByCode(type.getTypeCode());
        if (query != null) throw new PostResourceException("支付类型编码已存在");
        save(type);
    }

    @Override
    public void updateType(TypeInputDTO dto) throws ResourceNotFoundException {
        Type type = findTypeByCode(dto.getTypeCode());
        Type target = modelMapper.map(dto,Type.class);
        if (type == null) throw new ResourceNotFoundException();
        target.setId(type.getId());
        updateById(target);
    }

    @Override
    @Transactional
    public void delByIds(List<Integer> ids) throws PostResourceException {
        for (Integer id : ids){
            Type t = this.getById(id);
            LambdaQueryWrapper<Interface> interfaceQueryWrapper
                    = new LambdaQueryWrapper<>();
            interfaceQueryWrapper.eq(Interface::getPayTypeCode,t.getTypeCode());
            List<Interface> interfaceList = interfaceService.list(interfaceQueryWrapper);
            if (interfaceList == null || interfaceList.size() > 0){
                throw new PostResourceException("支付类型还有关联的支付接口，无法删除");
            }
            this.removeById(id);
        }
    }
}
