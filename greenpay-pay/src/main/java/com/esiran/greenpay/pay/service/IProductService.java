package com.esiran.greenpay.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 支付产品 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IProductService extends IService<Product> {
    Product getByProductCode(String productCode);
    List<ProductDTO> findAllProduct(ProductDTO productDTO);
    List<ProductDTO> findAllProductByPayTypeCode(String payTypeCode);
    IPage<ProductDTO> selectPage(IPage<ProductDTO> page, ProductQueryDTO productQueryDTO);
    int add(ProductInputDTO productInputDTO) throws PostResourceException;
    boolean updateById(Integer id, ProductInputDTO productInputDTO) throws PostResourceException, ResourceNotFoundException;
    void delByIds(List<Integer> ids);
}
