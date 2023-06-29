package com.esiran.greenpay.admin.controller.pay;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.Product;
import com.esiran.greenpay.pay.entity.Type;
import com.esiran.greenpay.pay.entity.TypeQueryDto;
import com.esiran.greenpay.pay.service.IProductService;
import com.esiran.greenpay.pay.service.ITypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pay/types")
public class APIAdminPayTypeController {
    private final ITypeService typeService;
    private final IProductService productService;
    public APIAdminPayTypeController(ITypeService typeService,
                                     IProductService productService) {
        this.typeService = typeService;
        this.productService = productService;
    }

    @GetMapping
    public IPage<Type> list(Page<Type> page , TypeQueryDto typeQueryDto){

        return typeService.selectPageByType(page,typeQueryDto);
    }

    @GetMapping("/{typeCode}/products")
    public List<Product> products(@PathVariable String typeCode){
        LambdaQueryWrapper<Product> queryWrapper =
                new QueryWrapper<Product>().lambda().eq(Product::getPayTypeCode,typeCode);
        return productService.list(queryWrapper);
    }
}
