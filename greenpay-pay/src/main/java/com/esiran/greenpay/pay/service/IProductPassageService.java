package com.esiran.greenpay.pay.service;

import com.esiran.greenpay.pay.entity.Product;
import com.esiran.greenpay.pay.entity.ProductPassage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 支付产品通道子账户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IProductPassageService extends IService<ProductPassage> {
    void removeByProductId(Integer productId);
    List<ProductPassage> listByProductId(Integer productId);
}
