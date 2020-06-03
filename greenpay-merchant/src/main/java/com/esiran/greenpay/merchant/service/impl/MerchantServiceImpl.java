package com.esiran.greenpay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.common.util.PercentCount;
import com.esiran.greenpay.common.util.RSAUtil;
import com.esiran.greenpay.merchant.entity.*;
import com.esiran.greenpay.merchant.mapper.MerchantMapper;
import com.esiran.greenpay.merchant.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p>
 * 商户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {
    private final ITypeService iTypeService;
    private final IProductService productService;
    private final IMerchantProductService merchantProductService;
    private final IMerchantProductPassageService merchantProductPassageService;
    private final IPassageAccountService passageAccountService;
    private final IApiConfigService apiConfigService;
    private final IPayAccountService payAccountService;
    private final IPrepaidAccountService prepaidAccountService;
    private final ISettleAccountService settleAccountService;
    private final IPassageService passageService;
    private final IMerchantAgentPayPassageService mchAgentPayPassageService;
    private final IAgentPayPassageService agentPayPassageService;
    private final IOrderService orderService;
    private final IAgentPayOrderService iAgentPayOrderService;
    private static final ModelMapper modelMapper = new ModelMapper();

    public MerchantServiceImpl(
            ITypeService iTypeService,
            IProductService productService,
            IMerchantProductService merchantProductService,
            IMerchantProductPassageService merchantProductPassageService, IPassageAccountService passageAccountService, IApiConfigService apiConfigService,
            IPayAccountService payAccountService,
            IPrepaidAccountService prepaidAccountService, ISettleAccountService settleAccountService, IPassageService passageService, IMerchantAgentPayPassageService mchAgentPayPassageService, IAgentPayPassageService agentPayPassageService, IOrderService orderService, IAgentPayOrderService iAgentPayOrderService) {
        this.iTypeService = iTypeService;
        this.productService = productService;
        this.merchantProductService = merchantProductService;
        this.merchantProductPassageService = merchantProductPassageService;
        this.passageAccountService = passageAccountService;
        this.apiConfigService = apiConfigService;
        this.payAccountService = payAccountService;
        this.prepaidAccountService = prepaidAccountService;
        this.settleAccountService = settleAccountService;
        this.passageService = passageService;
        this.mchAgentPayPassageService = mchAgentPayPassageService;
        this.agentPayPassageService = agentPayPassageService;
        this.orderService = orderService;
        this.iAgentPayOrderService = iAgentPayOrderService;
    }

    @Override
    public void updateMerchantInfoById(MerchantUpdateDTO dto, Integer id) throws Exception {
        Merchant target = this.getById(id);
        if (target == null) throw new Exception("商户不存在");
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<>();
        if (dto == null) return;
        updateWrapper.set(Merchant::getName,dto.getName());
        updateWrapper.set(Merchant::getEmail,dto.getEmail());
        updateWrapper.set(Merchant::getStatus,dto.getStatus());
        updateWrapper.set(Merchant::getUpdatedAt, LocalDateTime.now());
        updateWrapper.eq(Merchant::getId,target.getId());
        update(updateWrapper);
    }

    @Override
    @Transactional
    public void updateMerchantProduct(MerchantProductInputDTO dto, Integer id) throws Exception {
//        MerchantProduct mp = modelMapper.map(dto,MerchantProduct.class);
//        mp.setMerchantId(id);
//        Type type = iTypeService.findTypeByCode(mp.getPayTypeCode());
//        if (type == null) throw new Exception("未知支付类型");
//        Product product = productService.getDTOById(mp.getProductId());
//        if (product == null )  throw new Exception("支付产品不存在");
//        if (!product.getPayTypeCode().equals(type.getTypeCode()))
//            throw new Exception("支付产品不属于该支付类型");
//        LambdaUpdateWrapper<MerchantProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(MerchantProduct::getPayTypeCode,mp.getPayTypeCode());
//        merchantProductService.remove(lambdaUpdateWrapper);
//        merchantProductService.save(mp);
    }

    @Override
    public void updatePasswordById(String password, Integer id) throws Exception {
        Merchant target = this.getById(id);
        if (target == null) throw new Exception("商户不存在");
        if (StringUtils.isEmpty(password)) throw new Exception("密码不能为空");
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Merchant::getPassword, password);
        updateWrapper.set(Merchant::getUpdatedAt, LocalDateTime.now());
        updateWrapper.eq(Merchant::getId,target.getId());
        update(updateWrapper);
    }

    @Override
    public void updateSettleById(SettleAccountDTO settleAccountDTO, Integer id) throws Exception {
        Merchant merchant = this.getById(id);
        if (merchant == null) throw new Exception("商户不存在");
        SettleAccount target = modelMapper.map(settleAccountDTO,SettleAccount.class);
        target.setMerchantId(merchant.getId());
        LambdaQueryWrapper<SettleAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SettleAccount::getMerchantId, target.getMerchantId());
        SettleAccount src = settleAccountService.getOne(queryWrapper);
        if (src == null) throw new Exception("结算账户不存在");
        target.setId(src.getId());
        target.setUpdatedAt(LocalDateTime.now());
        settleAccountService.updateById(target);
    }

    @Override
    public void updateAccountBalance(Integer accType, Integer mchId, Double amount, Integer type, Integer action) throws Exception {
        if (amount == null || amount.floatValue() < 0.00f) throw new Exception("金额格式不正确");
        if (type == null)  throw new Exception("类型不能为空");
        long amountFen = Math.round(amount * 100);
        int availAmount = type == 1?(int) amountFen:0;
        int freezeAmount = type == 2?(int) amountFen:0;
        availAmount = action == 1 ? -availAmount:availAmount;
        freezeAmount = action == 1 ? -freezeAmount:freezeAmount;
        int i = accType == 1 ?
                    payAccountService.updateBalance(mchId,availAmount,freezeAmount):
                accType == 2 ?
                    prepaidAccountService.updateBalance(mchId,availAmount,freezeAmount)
                : -1;
        if (i == -1) throw new Exception("账户类型不正确");
        if (i == 0) throw new Exception("账户余额不足");
    }

    @Override
    public MerchantDetailDTO findMerchantById(Integer id) {
        Merchant merchant = getById(id);
        MerchantDetailDTO dto = modelMapper.map(merchant, MerchantDetailDTO.class);
        ApiConfigDTO apiConfigDTO = apiConfigService.findByMerchantId(merchant.getId());
        String publicKeyVal = null;
        String privateKeyVal = null;
        String mchPublicKeyVal = null;
        if (!StringUtils.isEmpty(apiConfigDTO.getPubKey())){
            publicKeyVal = RSAUtil.formatKeyPem(
                    RSAUtil.PEM_FILE_PUBLIC_PKCS1_BEGIN,
                    apiConfigDTO.getPubKey(),
                    RSAUtil.PEM_FILE_PUBLIC_PKCS1_END);
        }
        if (!StringUtils.isEmpty(apiConfigDTO.getPrivateKey())){
            privateKeyVal = RSAUtil.formatKeyPem(
                    RSAUtil.PEM_FILE_PRIVATE_PKCS8_BEGIN,
                    apiConfigDTO.getPrivateKey(),
                    RSAUtil.PEM_FILE_PRIVATE_PKCS8_BEGIN);
        }
        if (!StringUtils.isEmpty(apiConfigDTO.getMchPubKey())){
            mchPublicKeyVal = RSAUtil.formatKeyPem(
                    RSAUtil.PEM_FILE_PUBLIC_PKCS1_BEGIN,
                    apiConfigDTO.getMchPubKey(),
                    RSAUtil.PEM_FILE_PUBLIC_PKCS1_END);
        }
//        RSAUtil.verify()
        apiConfigDTO.setPubKeyVal(publicKeyVal);
        apiConfigDTO.setPrivateKeyVal(privateKeyVal);
        apiConfigDTO.setMchPubKeyVal(mchPublicKeyVal);
        PayAccountDTO payAccountDTO = payAccountService.findByMerchantId(merchant.getId());
        PrepaidAccountDTO prepaidAccountDTO = prepaidAccountService.findByMerchantId(merchant.getId());
        SettleAccountDTO settleAccountDTO = settleAccountService.findByMerchantId(merchant.getId());
        dto.setApiConfig(apiConfigDTO);
        dto.setPayAccount(payAccountDTO);
        dto.setPrepaidAccount(prepaidAccountDTO);
        dto.setSettleAccount(settleAccountDTO);
        return dto;
    }

    @Override
    @Transactional
    public void addMerchant(MerchantInputDTO merchantInputDTO) throws Exception {
        Merchant merchant = modelMapper.map(merchantInputDTO,Merchant.class);
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getUsername, merchant.getUsername()).or()
                .eq(Merchant::getEmail, merchant.getEmail());
        Merchant oldMerchant = getOne(queryWrapper);
        if (oldMerchant != null){
            throw new PostResourceException("用户名或邮箱已存在");
        }
        save(merchant);
        // api 配置信息构造
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String privateKey = RSAUtil.getPrivateKey(keyPair);
        String publicKey = RSAUtil.getPublicKey(keyPair);
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setMchId(merchant.getId());
        apiConfig.setApiKey(EncryptUtil.md5(EncryptUtil.baseTimelineCode()));
        apiConfig.setApiSecurity(EncryptUtil.md5(EncryptUtil.baseTimelineCode()));
        apiConfig.setPrivateKey(privateKey);
        apiConfig.setPubKey(publicKey);
        apiConfigService.save(apiConfig);
        // 商户账户信息初始化
        PayAccount payAccount = new PayAccount();
        payAccount.setMerchantId(merchant.getId());
        payAccount.setAvailBalance(0);
        payAccount.setFreezeBalance(0);
        payAccountService.save(payAccount);
        PrepaidAccount prepaidAccount = new PrepaidAccount();
        prepaidAccount.setMerchantId(merchant.getId());
        prepaidAccount.setAvailBalance(0);
        prepaidAccount.setFreezeBalance(0);
        prepaidAccountService.save(prepaidAccount);
        SettleAccount settleAccount = new SettleAccount();
        settleAccount.setMerchantId(merchant.getId());
        settleAccount.setSettleFeeType(1);
        settleAccount.setSettleFeeRate(new BigDecimal("0.0"));
        settleAccount.setSettleFeeAmount(0);
        settleAccount.setStatus(false);
        settleAccount.setCreatedAt(LocalDateTime.now());
        settleAccountService.save(settleAccount);
    }

    @Override
    public List<MerchantProductDTO> selectMchProductById(Integer mchId) throws APIException, ResourceNotFoundException {
        Merchant merchant = getById(mchId);
        if (merchant == null){
            throw new APIException("商户号不存在","MERCHANT_NOT_FOUND");
        }
        List<Product> products = productService.list();
        List<MerchantProductDTO> mps = new ArrayList<>();
        for (Product product : products){
            MerchantProductDTO mp =  this.selectMchProductById(mchId,product.getId());
            mps.add(mp);
        }
        return mps;
    }

    public static MerchantAgentPayPassageDTO buildMerchantAgentPayPassageDTO(
            Integer mchId,
            AgentPayPassage agentPayPassage){
        MerchantAgentPayPassageDTO dto = new MerchantAgentPayPassageDTO();
        dto.setMerchantId(mchId);
        dto.setPassageId(agentPayPassage.getId());
        dto.setPassageName(agentPayPassage.getPassageName());
        dto.setStatus(false);
        return dto;
    }

    @Override
    public List<MerchantAgentPayPassageDTO> listMchAgentPayPassageByMchId(Integer mchId) {
        List<AgentPayPassage> agentPayPassages = agentPayPassageService.list();
        List<MerchantAgentPayPassageDTO> al = new ArrayList<>();
        for (AgentPayPassage app : agentPayPassages){
            MerchantAgentPayPassageDTO dto = mchAgentPayPassageService.getOneDTOByMchId(mchId,app.getId());
            if (dto == null){
                dto = buildMerchantAgentPayPassageDTO(mchId,app);
            }
            al.add(dto);
        }
        return al;
    }

    @Override
    public MerchantAgentPayPassageDTO selectMchAgentPayPassageByMchId(Integer mchId, Integer passageId) {
        AgentPayPassage app = agentPayPassageService.getById(passageId);
        if (app == null) return null;
        MerchantAgentPayPassageDTO data = mchAgentPayPassageService.getOneDTOByMchId(mchId,passageId);
        if (data == null){
            data = buildMerchantAgentPayPassageDTO(mchId,app);
        }
        return data;
    }


    @Override
    public MerchantProductDTO selectMchProductById(Integer mchId, Integer productId) {
        Product product = productService.getById(productId);
        if (product == null){
            return null;
        }
        MerchantProductDTO mp =  merchantProductService.getByProductId(mchId,product.getId());
        if (mp == null){
            mp = new MerchantProductDTO();
            mp.setMerchantId(mchId);
            mp.setProductId(product.getId());
            mp.setProductCode(product.getProductCode());
            mp.setProductName(product.getProductName());
            mp.setProductType(product.getProductType());
            mp.setPayTypeCode(product.getPayTypeCode());
            mp.setInterfaceMode(1);
            mp.setStatus(false);
        }
        mp.setRateDisplay(NumberUtil.twoDecimals(mp.getRate()));
        return mp;
    }

    @Override
    public IPage<MerchantDTO> selectMerchantByPage(IPage<Void> page) {
        IPage<Merchant> merchantPage = this.page(new Page<>(page.getCurrent(),page.getSize()));
        List<Merchant> merchants = merchantPage.getRecords();
        IPage<MerchantDTO> merchantDTOIPage = merchantPage.convert(item-> modelMapper.map(item,MerchantDTO.class));
        List<MerchantDTO> merchantDTOList = merchants.stream().map(item->{
            MerchantDTO dto = modelMapper.map(item,MerchantDTO.class);
            PayAccountDTO payAccount = payAccountService.findByMerchantId(dto.getId());
            PrepaidAccountDTO prepaidAccountDTO = prepaidAccountService.findByMerchantId(dto.getId());
            SettleAccountDTO settleAccountDTO = settleAccountService.findByMerchantId(dto.getId());
            dto.setPayAccount(payAccount);
            dto.setPrepaidAccount(prepaidAccountDTO);
            dto.setSettleAccountDTO(settleAccountDTO);
            return dto;
        }).collect(Collectors.toList());
        merchantDTOIPage.setRecords(merchantDTOList);
        return merchantDTOIPage;
    }


    private PassageAndSubAccount passageAndSubAccount(Integer passageId, Integer accId){
        Passage passage = passageService.getById(passageId);
        PassageAccount passageAccount = passageAccountService.getById(accId);
        if (passage == null || !passage.getStatus()) return null;
        if (passageAccount == null || !passageAccount.getStatus()) return null;
        return new PassageAndSubAccount(passage,passageAccount);
    }

    /**
     * 根据权重随机选择
     * @param w 权重数组
     * @return 索引
     */
    private int randomPickIndex(int[] w){
        int len = w.length;
        if (len == 0) return -1;
        if (len == 1) return 0;
        int bound = w[len-1];
        Random random = new Random(System.currentTimeMillis());
        int val = random.nextInt(bound)+1;
        int left = 0, right = len-1, mid;
        while (left < right){
            mid = (right - left) / 2 + left;
            if (w[mid] == val){
                return mid;
            }else if(w[mid] > val){
                right = mid;
            }else {
                left = mid + 1;
            }
        }
        return left;
    }
    private Passage solutionPickMchProductPassage(Integer mchId, Integer productId){
        // 获取可用已配置的商户产品通道，按权重值降序排列
        List<MerchantProductPassage> mpps = merchantProductPassageService
                .listAvailable(mchId, productId);
        if (mpps == null||mpps.size() == 0) return null;
        // 构造权重区间值数组
        int[] sumArr = new int[mpps.size()];
        // 权重总和
        int sum = 0;
        for (int i=0; i<sumArr.length; i++){
            MerchantProductPassage productPassage = mpps.get(i);
            int w = productPassage.getWidget();
            sum += w;
            sumArr[i] = sum;
        }
        // 根据权重随机获取数组索引
        int index = randomPickIndex(sumArr);
        // 根据索引获取产品通道
        MerchantProductPassage mpp = mpps.get(index);
        if (mpp == null) return null;
        Passage passage = passageService.getById(mpp.getPassageId());
        if (passage == null||!passage.getStatus())
            return null;
        return passage;
    }

    private PassageAccount solutionPickPassageAcc(Integer passageId){
        List<PassageAccount> pas = passageAccountService
                .listAvailable(passageId);
        if (pas == null||pas.size()==0) return null;
        // 构造权重区间值数组
        int[] sumArr = new int[pas.size()];
        // 权重总和
        int sum = 0;
        for (int i=0; i<sumArr.length; i++){
            PassageAccount productPassage = pas.get(i);
            int w = productPassage.getWeight();
            sum += w;
            sumArr[i] = sum;
        }
        // 根据权重随机获取数组索引
        int index = randomPickIndex(sumArr);
        PassageAccount pa = pas.get(index);
        if (pa == null || !pa.getStatus()) return null;
        return pa;
    }

    @Override
    public PassageAndSubAccount scheduler(Integer mchId, Integer productId) {
        Merchant mch = getById(mchId);
        if (mch == null)  return null;
        // 查询商户支持的产品
        MerchantProductDTO mpd = merchantProductService
                .getAvailableByProductId(mchId,productId);
        if (mpd == null) return null;
        // 获取定义的接口模式
        Integer inf = mpd.getInterfaceMode();
        if (inf == null) return null;
        PassageAndSubAccount pas = null;
        if (inf.equals(1)){
            // 接口模式为单独，直接查询配置的默认通道和子账户
            pas = passageAndSubAccount(mpd.getDefaultPassageId(),mpd.getDefaultPassageAccId());
        }else if(inf.equals(2)){
            // 接口模式为轮训，权重随机策略，选择通道和子账户
            Passage passage = solutionPickMchProductPassage(mchId,productId);
            if (passage == null) return null;
            PassageAccount passageAccount = solutionPickPassageAcc(passage.getId());
            if (passageAccount == null) return null;
            pas = new PassageAndSubAccount(passage,passageAccount);
        }
        if (pas == null) return null;
        pas.setProductRate(mpd.getRate());
        return pas;
    }

    @Override
    public MerchantAgentPayPassage schedulerAgentPayPassage(Integer mchId) {
        List<MerchantAgentPayPassage> passages = mchAgentPayPassageService.listAvailableByMchId(mchId);
        if (passages == null || passages.size() == 0) return null;
        // 构造权重区间值数组
        int[] sumArr = new int[passages.size()];
        // 权重总和
        int sum = 0;
        for (int i=0; i<sumArr.length; i++){
            MerchantAgentPayPassage mapp = passages.get(i);
            int w = mapp.getWeight();
            sum += w;
            sumArr[i] = sum;
        }
        // 根据权重随机获取数组索引
        int index = randomPickIndex(sumArr);
        MerchantAgentPayPassage pa = passages.get(index);
        if (pa == null || !pa.getStatus()) return null;
        return pa;
    }

    @Override
    public AgentPayPassageAccount schedulerAgentPayPassageAcc(Integer mchId, Integer passageId) {
        return null;
    }

    @Override
    public HashMap<String,Object> homeData(Integer mchId) {

        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();
        List<HashMap<String,Object>> statistics = new ArrayList<>();


        PayAccountDTO payAccountDTO = payAccountService.findByMerchantId(mchId);
        PrepaidAccountDTO prepaidAccountDTO = prepaidAccountService.findByMerchantId(mchId);
        List<Order> orders = orderService.getByDay(mchId);

        //收单总数
        int totalCount = orders.size();
        //收单总额
        int totalMoney = orders.stream().mapToInt(Order::getAmount).sum();
        int successCount = (int) orders.stream().filter(order -> 2 == order.getStatus()).count();
        int successMoney = orders.stream().filter(order -> 2 == order.getStatus()).mapToInt(Order::getAmount).sum();


        //今日所有订单
        List<AgentPayOrderDTO> intradayOrders = iAgentPayOrderService.findIntradayOrders(mchId);
        //昨日所有订单
        List<AgentPayOrderDTO> yesterdayOrders = iAgentPayOrderService.findYesterdayOrders(mchId);

        //今日收单笔数
        Integer intraDayOrdersCount = intradayOrders.size();

        //今日收单总额
        Integer intrDayOrderAmounts = intradayOrders.stream().mapToInt(AgentPayOrderDTO::getAmount).sum();

        //今日成交笔数
        Long  intrDayOrderCountSucces = intradayOrders.stream().filter(agentPayOrderDTO -> agentPayOrderDTO.getStatus()==3).count();
        //今日成交总额
        Integer  intrDayOrderAmountSucces = intradayOrders.stream().filter(agentPayOrderDTO -> agentPayOrderDTO.getStatus()==3).mapToInt(AgentPayOrderDTO::getAmount).sum();


        //昨日收单笔数
        Integer yesterDayOrdersCount = intradayOrders.size();
        //昨日成交笔数
        Long yesterDayOrdersCountSucces = yesterdayOrders.stream().filter(agentPayOrderDTO -> agentPayOrderDTO.getStatus()==3).count();
        //昨日成交总额
        Integer yesterDayOrdersAmountSucces = yesterdayOrders.stream().filter(agentPayOrderDTO -> agentPayOrderDTO.getStatus()==3).mapToInt(AgentPayOrderDTO::getAmount).sum();
        //同比昨日
        String format = percentBigDecimal(new BigDecimal( intraDayOrdersCount), new BigDecimal(yesterDayOrdersCount));


        data.put("name", "今日收单笔数");
        data.put("val",intraDayOrdersCount);
        data.put("val2",format);
        data.put("rightHint", "日同比");
        data.put("leftHint","昨日");
        data.put("upDay",yesterDayOrdersCount);
        statistics.add(data);



        //转换率 订单总数
        BigDecimal a = new BigDecimal(intrDayOrderCountSucces);
        BigDecimal b = new BigDecimal(yesterDayOrdersCountSucces);
        String percent4Count = p.percentBigDecimal(a, b);


        data = new HashMap<>();
        data.put("name", "今日成交笔数");
        data.put("val", intrDayOrderCountSucces);
        data.put("val2",percent4Count);
        data.put("rightHint", "日同比");
        data.put("leftHint","昨日");
        data.put("upDay",yesterDayOrdersCountSucces);
        statistics.add(data);



        a = new BigDecimal(yesterDayOrdersAmountSucces);
        b = new BigDecimal(intrDayOrderAmountSucces);
        String percent4Amount = p.percentBigDecimal(a, b);

        data = new HashMap<>();
        data.put("name", "今日成交总额");
        data.put("val", NumberUtil.amountFen2Yuan(intrDayOrderAmountSucces));

        data.put("val2", percent4Amount);
        data.put("rightHint", "日同比");
        data.put("leftHint","昨日");
        data.put("upDay",yesterDayOrdersAmountSucces);
        statistics.add(data);


        map.put("statistics", statistics);
        //交易趋势

        //交易趋势


        //订单总额


        //七日订单数据


        //七日订单数据


        HomeData homeData = new HomeData();
        homeData.setPayAccountDTO(payAccountDTO);
        homeData.setPrepaidAccountDTO(prepaidAccountDTO);
        homeData.setTotalCount(totalCount);
        homeData.setSuccessCount(successCount);
        homeData.setTotalMoney(NumberUtil.amountFen2Yuan(totalMoney));
        homeData.setSuccessMoney(NumberUtil.amountFen2Yuan(successMoney));

        homeData.setIntraDayOrdersCount(intraDayOrdersCount);
        homeData.setIntrDayOrderAmounts(NumberUtil.amountFen2Yuan(intrDayOrderAmounts));
        homeData.setIntrDayOrderAmountSucces(NumberUtil.amountFen2Yuan(intrDayOrderAmountSucces));
        homeData.setYesterDayOrdersAmountSucces(NumberUtil.amountFen2Yuan(yesterDayOrdersAmountSucces));


        return map;
    }



    private PercentCount p = new PercentCount();
    private String percentBigDecimal(BigDecimal total,BigDecimal num){

        String percent = p.percentBigDecimal(total,num);
        return percent;
    }


    @Override
    @Transactional(rollbackFor = {PostResourceException.class,Exception.class})
    public Boolean delMerchant(Integer mchid) throws PostResourceException {

        //查询到当前商户
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getId, mchid);
        Merchant merchant = getOne(queryWrapper);
        if (merchant == null) {
            throw new PostResourceException("未查询到商户信息");
        }
        removeById(merchant.getId());

        //查询并删除API配置信息
        apiConfigService.removeByMerchantId(mchid);

        //查询商户账户信息并删除
        payAccountService.removeByMerchantId(mchid);

        //查询预付款账户并删除
        prepaidAccountService.removeByMerchantId(mchid);

        //查询商户结算账户并删除
        settleAccountService.removeByMerchantId(mchid);

        //删除商户产品
        merchantProductService.removeByMerchantId(mchid);

        //删除商户产品通道
        merchantProductPassageService.removeByMerchantId(mchid);

        //删除商户代理通道
        mchAgentPayPassageService.removeByMerchantId(mchid);

        return true;
    }
}
