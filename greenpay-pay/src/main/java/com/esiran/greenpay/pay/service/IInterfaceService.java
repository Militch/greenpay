package com.esiran.greenpay.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.Interface;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.pay.entity.InterfaceDTO;
import com.esiran.greenpay.pay.entity.InterfaceInputDTO;
import com.esiran.greenpay.pay.entity.InterfaceQueryDTO;

import java.util.List;

/**
 * <p>
 * 支付接口 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IInterfaceService extends IService<Interface> {
    Interface getByCode(String code);
    List<Interface> listByPayTypeCode(String payTypeCode);
    InterfaceDTO getDTOByCode(String code);
    IPage<InterfaceDTO> selectPage(IPage<InterfaceDTO> iPage, InterfaceQueryDTO interfaceDTO);
    int addInterface(InterfaceInputDTO interfaceInputDTO) throws PostResourceException;
    boolean updateById(Integer id, InterfaceInputDTO interfaceInputDTO) throws PostResourceException, ResourceNotFoundException;
    void delByIds(List<Integer> ids) throws PostResourceException;
}
