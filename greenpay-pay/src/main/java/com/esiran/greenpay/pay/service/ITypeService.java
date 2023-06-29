package com.esiran.greenpay.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.Type;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.pay.entity.TypeDTO;
import com.esiran.greenpay.pay.entity.TypeInputDTO;
import com.esiran.greenpay.pay.entity.TypeQueryDto;

import java.util.List;

/**
 * <p>
 * 支付类型 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface ITypeService extends IService<Type> {
    Type findTypeByCode(String code);
    TypeDTO getTypeByCode(String code);
    List<Type> listByType(Integer type);
    List<Type> listByPayType();
    List<Type> listByAgentPayType();

    IPage<Type> selectPageByType(IPage<Type> page, TypeQueryDto queryDto);
    void saveType(TypeInputDTO dto) throws PostResourceException;
    void updateType(TypeInputDTO dto) throws ResourceNotFoundException;
    void delByIds(List<Integer> ids) throws PostResourceException;
}
