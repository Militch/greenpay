package com.esiran.greenpay.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 支付通道 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IPassageService extends IService<Passage> {
    List<Passage> listByPayTypeCode(String payTypeCode);
    IPage<PassageDTO> selectPage(IPage<PassageDTO> page, PassageQueryDTO passageQueryDTO);
    int add(PassageInputDTO passageInputDTO) throws PostResourceException;
    boolean updateById(Integer id, PassageInputDTO passageInputDTO) throws PostResourceException, ResourceNotFoundException;
    void delByIds(List<Integer> ids) throws PostResourceException;
}
