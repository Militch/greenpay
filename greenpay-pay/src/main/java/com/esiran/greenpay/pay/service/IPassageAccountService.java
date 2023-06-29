package com.esiran.greenpay.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 支付通道账户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IPassageAccountService extends IService<PassageAccount> {
    List<PassageAccount> listAvailable(Integer passageId);
    List<PassageAccount> listByPayTypeCode(String payTypeCode);
    PassageAccountDTO getDTOById(Integer id);
    IPage<PassageAccountDTO> selectPage(IPage<PassageAccountDTO> page, PassageAccountDTO passageAccountDTO);
    int add(PassageAccountInputDTO passageAccountInputDTO) throws PostResourceException;
    boolean updateById(Integer id, PassageAccountInputDTO passageAccountInputDTO) throws PostResourceException, ResourceNotFoundException;
    void delIds(List<Integer> ids);
}
