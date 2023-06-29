package com.esiran.greenpay.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.pay.entity.ProductPassage;
import com.esiran.greenpay.pay.mapper.ProductPassageMapper;
import com.esiran.greenpay.pay.service.IProductPassageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 支付产品通道子账户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class ProductPassageServiceImpl extends ServiceImpl<ProductPassageMapper, ProductPassage> implements IProductPassageService {

    @Override
    public void removeByProductId(Integer productId) {
        LambdaUpdateWrapper<ProductPassage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProductPassage::getProductId,productId);
        remove(updateWrapper);
    }

    @Override
    public List<ProductPassage> listByProductId(Integer productId) {
        LambdaQueryWrapper<ProductPassage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductPassage::getProductId,productId);
        return list(queryWrapper);
    }
}
