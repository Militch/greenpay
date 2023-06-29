package com.esiran.greenpay.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.mapper.ProductMapper;
import com.esiran.greenpay.pay.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 支付产品 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final Gson gson = new GsonBuilder().create();
    private final ITypeService typeService;
    private final IPassageService passageService;
    private final IPassageAccountService passageAccountService;
    private final IProductPassageService productPassageService;
    public ProductServiceImpl(
            ITypeService typeService,
            IPassageService passageService,
            IPassageAccountService passageAccountService,
            IProductPassageService productPassageService) {
        this.typeService = typeService;
        this.passageService = passageService;
        this.passageAccountService = passageAccountService;
        this.productPassageService = productPassageService;
    }

    @Override
    public Product getByProductCode(String productCode) {
        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Product::getProductCode,productCode);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public List<ProductDTO> findAllProduct(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO,Product.class);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntity(product);
        List<Product> productList = this.list(queryWrapper);
        return productList.stream()
                .map(item->modelMapper.map(item,ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> findAllProductByPayTypeCode(String payTypeCode) {
        Type type = typeService.findTypeByCode(payTypeCode);
        if (type == null) return null;
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getPayTypeCode,type.getTypeCode());
        List<Product> productList = this.list(queryWrapper);
        List<ProductDTO> list = productList.stream()
                .map(item->modelMapper.map(item,ProductDTO.class))
                .collect(Collectors.toList());
        return list.stream()
                .peek(item-> item.setPayTypeName(type.getTypeName()))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<ProductDTO> selectPage(IPage<ProductDTO> page, ProductQueryDTO productDTO) {
        Product map = modelMapper.map(productDTO, Product.class);

        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Product::getId);
        lambdaQueryWrapper.setEntity(map);

        IPage<Product> productPage = this.page(new Page<>(page.getCurrent(),page.getSize()),lambdaQueryWrapper);
        return productPage.convert(item->modelMapper.map(item,ProductDTO.class));
    }

    @Override
    public int add(ProductInputDTO productInputDTO) throws PostResourceException {
        Product old = this.getByProductCode(productInputDTO.getProductCode());
        if (old != null) throw new PostResourceException("产品编码已存在");
        Product target = modelMapper.map(productInputDTO,Product.class);
        TypeDTO typeDTO = typeService.getTypeByCode(target.getPayTypeCode());
        if (typeDTO == null) throw new PostResourceException("支付类型不存在");
        save(target);
        return target.getId();
    }

    @Override
    public boolean updateById(Integer id, ProductInputDTO productInputDTO) throws PostResourceException, ResourceNotFoundException {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Product src = this.getById(id);
        if (src == null) throw new ResourceNotFoundException("支付产品不存在");

        Product target = modelMapper.map(productInputDTO,Product.class);
        target.setId(src.getId());
        TypeDTO typeDTO = typeService.getTypeByCode(target.getPayTypeCode());
        if (typeDTO == null) throw new PostResourceException("支付类型不存在");
        if (!target.getPayTypeCode().equals(src.getPayTypeCode())){
            throw new PostResourceException("支付类型无法修改");
        }
        if (!target.getProductCode().equals(src.getPayTypeCode())){
            Product old = this.getByProductCode(productInputDTO.getProductCode());
            if (old == null) throw new PostResourceException("产品编码已存在");
        }
        if (productInputDTO.getDefaultPassageId() != null){
            Passage passage = passageService.getById(productInputDTO.getDefaultPassageId());
            if (productInputDTO.getDefaultPassageAccId() == null)
                throw new PostResourceException("支付通道子账户不能为空");
            if (passage == null)
                throw new PostResourceException("支付通道不存在");
            PassageAccount passageAcc = passageAccountService.getById(productInputDTO.getDefaultPassageAccId());
            if (passageAcc == null)
                throw new PostResourceException("支付通道子账户不存在");
            if (!passage.getId().equals(passageAcc.getPassageId())){
                throw new PostResourceException("支付通道与子账户不匹配");
            }
        }
        String loopPassages = productInputDTO.getLoopPassages();
        if (!StringUtils.isEmpty(loopPassages)){
            List<ProductPassageInputDTO> passageInputDTOS = gson.fromJson(loopPassages,
                    new TypeToken<List<ProductPassageInputDTO>>(){}.getType());
            List<ProductPassageInputDTO> passagesDTOs = passageInputDTOS.stream()
                    .filter(ProductPassageInputDTO::getUsage)
                    .collect(Collectors.toList());
            List<ProductPassage> passages = passagesDTOs.stream()
                    .map(item->modelMapper.map(item,ProductPassage.class))
                    .collect(Collectors.toList());
            productPassageService.removeByProductId(target.getId());
            passages.forEach(productPassageService::save);
        }
        target.setUpdatedAt(LocalDateTime.now());
        return updateById(target);
    }

    @Override
    @Transactional
    public void delByIds(List<Integer> ids) {
        for (Integer id : ids){
            this.removeById(id);
            LambdaQueryWrapper<ProductPassage> productPassageLambdaQueryWrapper
                    = new LambdaQueryWrapper<>();
            productPassageLambdaQueryWrapper.eq(ProductPassage::getProductId,id);
            productPassageService.remove(productPassageLambdaQueryWrapper);
        }
    }
}
