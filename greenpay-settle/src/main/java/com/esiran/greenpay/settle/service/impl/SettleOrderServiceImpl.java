package com.esiran.greenpay.settle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.IdWorker;
import com.esiran.greenpay.common.util.MoneyFormatUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.common.util.PercentCount;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.entity.SettleAccountDTO;
import com.esiran.greenpay.merchant.entity.StatisticDTO;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.merchant.service.IPayAccountService;
import com.esiran.greenpay.merchant.service.ISettleAccountService;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import com.esiran.greenpay.pay.entity.ExtractQueryDTO;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.service.IOrderService;
import com.esiran.greenpay.settle.entity.SettleOrder;
import com.esiran.greenpay.settle.entity.SettleOrderDTO;
import com.esiran.greenpay.settle.entity.SettleOrderInputDTO;
import com.esiran.greenpay.settle.entity.SettleOrderQueryDto;
import com.esiran.greenpay.settle.mapper.SettleOrderMapper;
import com.esiran.greenpay.settle.service.ISettleOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商户结算订单 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class SettleOrderServiceImpl extends ServiceImpl<SettleOrderMapper, SettleOrder> implements ISettleOrderService {

    private static final ModelMapper modelMapper = new ModelMapper();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final IPayAccountService payAccountService;
    private final  IMerchantService iMerchantService;
    private final ISettleAccountService iSettleAccountService;
    private final IdWorker idWorker;
    private final IOrderService orderService;
    private final ISettleAccountService settleAccountService;
    public SettleOrderServiceImpl(IPayAccountService payAccountService, IMerchantService iMerchantService, ISettleAccountService iSettleAccountService, IdWorker idWorker, IOrderService orderService, ISettleAccountService settleAccountService) {
        this.payAccountService = payAccountService;
        this.iMerchantService = iMerchantService;
        this.iSettleAccountService = iSettleAccountService;
        this.idWorker = idWorker;
        this.orderService = orderService;
        this.settleAccountService = settleAccountService;
    }


    public static SettleOrderDTO convertOrderEntity(SettleOrder order){
        if (order == null) return null;
        SettleOrderDTO dto = modelMapper.map(order,SettleOrderDTO.class);
        dto.setAmountDisplay(NumberUtil.amountFen2Yuan(order.getAmount()));
        dto.setFeeDisplay(NumberUtil.amountFen2Yuan(order.getFee()));
        dto.setSettleAmountDisplay(NumberUtil.amountFen2Yuan(order.getSettleAmount()));
        String status = order.getStatus() == 1 ? "待审核"
                : order.getStatus() == 2 ? "待处理"
                : order.getStatus() == 3 ? "处理中"
                : order.getStatus() == 4 ? "已结算"
                : order.getStatus() == -1 ? "已驳回"
                : order.getStatus() == -2 ? "结算失败"
                : "未知";
        dto.setStatusDisplay(status);
        dto.setCreatedAtDisplay(dtf.format(order.getCreatedAt()));
        dto.setUpdatedAtDisplay(dtf.format(order.getUpdatedAt()));
        if (dto.getSettledAt() != null)
            dto.setSettledAtDisplay(dtf.format(dto.getSettledAt()));
        return dto;
    }
    @Override
    public IPage<SettleOrderDTO> selectPage(IPage<SettleOrderDTO> page, SettleOrderDTO orderDTO ) {
        SettleOrder settleOrder = modelMapper.map(orderDTO,SettleOrder.class);
        LambdaQueryWrapper<SettleOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SettleOrder::getStatus,orderDTO.getStatus());
        queryWrapper.orderByDesc(SettleOrder::getCreatedAt);
        queryWrapper.setEntity(settleOrder);
        IPage<SettleOrder> orderPage = this.page(new Page<>(page.getCurrent(),page.getSize()),queryWrapper);
        return orderPage.convert(SettleOrderServiceImpl::convertOrderEntity);
    }


    @Override
    public IPage<SettleOrderDTO> selectPageByAudit(IPage<SettleOrderDTO> page, SettleOrderQueryDto settleOrderQueryDto) {
        LambdaQueryWrapper<SettleOrder> queryWrapper = new LambdaQueryWrapper<>();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SettleOrder settleOrder = modelMapper.map(settleOrderQueryDto, SettleOrder.class);
        queryWrapper.ge(SettleOrder::getStatus,-1);
        queryWrapper.lt(SettleOrder::getStatus,2);
        queryWrapper.orderByDesc(SettleOrder::getCreatedAt);
        if (settleOrderQueryDto.getStartTime() != null) {
            queryWrapper.ge(SettleOrder::getCreatedAt, settleOrderQueryDto.getStartTime());
        }
        if (settleOrderQueryDto.getEndTime() != null) {
            queryWrapper.lt(SettleOrder::getCreatedAt, settleOrderQueryDto.getEndTime());
        }
        queryWrapper.setEntity(settleOrder);
        IPage<SettleOrder> orderPage = this.page(new Page<>(page.getCurrent(),page.getSize()),queryWrapper);
        return orderPage.convert(SettleOrderServiceImpl::convertOrderEntity);
    }

    @Override
    public IPage<SettleOrderDTO> selectPageByPayable(IPage<SettleOrderDTO> page ,SettleOrderQueryDto settleOrderQueryDto) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SettleOrder settleOrder = modelMapper.map(settleOrderQueryDto, SettleOrder.class);
        LambdaQueryWrapper<SettleOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(SettleOrder::getStatus,2);
        queryWrapper.orderByDesc(SettleOrder::getCreatedAt);

        if (settleOrderQueryDto.getStartTime() != null) {
            queryWrapper.ge(SettleOrder::getCreatedAt, settleOrderQueryDto.getStartTime());
        }

        if (settleOrderQueryDto.getEndTime()!=null) {
            queryWrapper.le(SettleOrder::getCreatedAt, settleOrderQueryDto.getEndTime());
        }
        queryWrapper.setEntity(settleOrder);
        IPage<SettleOrder> orderPage = this.page(new Page<>(page.getCurrent(),page.getSize()),queryWrapper);
        return orderPage.convert(SettleOrderServiceImpl::convertOrderEntity);
    }

    @Override
    public IPage<SettleOrderDTO> findPageByMchId(IPage<SettleOrderDTO> page, Integer mchId) {
        LambdaQueryWrapper<SettleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SettleOrder::getCreatedAt);
        wrapper.eq(SettleOrder::getMchId,mchId);
        IPage<SettleOrder> settleOrderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return settleOrderPage.convert(SettleOrderServiceImpl::convertOrderEntity);
    }

    @Override
    public IPage<SettleOrderDTO> findPageByQuery(IPage<SettleOrderDTO> page, Integer mchId, ExtractQueryDTO queryDTO) {
        LambdaQueryWrapper<SettleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SettleOrder::getCreatedAt);
        wrapper.eq(SettleOrder::getMchId,mchId);
        if (!StringUtils.isEmpty(queryDTO.getOrderNo())){
            wrapper.eq(SettleOrder::getOrderNo,queryDTO.getOrderNo());
        }
        if (!StringUtils.isEmpty(queryDTO.getAccountName())){
            wrapper.eq(SettleOrder::getAccountName,queryDTO.getAccountName());
        }
        if (!StringUtils.isEmpty(queryDTO.getStatus())){
            wrapper.eq(SettleOrder::getStatus,queryDTO.getStatus());
        }
        if (!StringUtils.isEmpty(queryDTO.getStartTime())){
            wrapper.ge(SettleOrder::getCreatedAt,queryDTO.getStartTime());
        }
        if (!StringUtils.isEmpty(queryDTO.getEndTime())){
            wrapper.lt(SettleOrder::getCreatedAt,queryDTO.getEndTime());
        }
        IPage<SettleOrder> settleOrderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return settleOrderPage.convert(SettleOrderServiceImpl::convertOrderEntity);
    }

    @Override
    public SettleOrderDTO getByOrderNo(String orderNo) {
        LambdaQueryWrapper<SettleOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SettleOrder::getOrderNo, orderNo);
        SettleOrder order = this.getOne(lambdaQueryWrapper);
        if (order == null) return null;
        return SettleOrderServiceImpl.convertOrderEntity(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(String orderNo, Integer updateStatus) throws PostResourceException {
        SettleOrderDTO orderDTO = getByOrderNo(orderNo);
        if (orderDTO == null) {
            throw new PostResourceException("订单不存在");
        }

        //驳回逻辑
        SettleOrder settleOrder = getById(orderDTO.getId());
        //状态（1：待审核，2：待处理，3：处理中，4：已结算，-1：已驳回，-2：结算失败）
        int old_stat = settleOrder.getStatus();

        //其它状态修改
        if (old_stat == -1) {
            throw new PostResourceException("订单状态：已驳回");
        }else
        if (old_stat == -2) {
            throw new PostResourceException("订单状态：结算失败");
        }else
        if (old_stat == 4) {
            throw new PostResourceException("订单状态：已结算");
        }

        if (updateStatus == 1) {
            throw new PostResourceException("订单状态：待审核");
        }

        //反还用户冻结金额
        if (updateStatus == -1) {
            int result = payAccountService.updateBalance(orderDTO.getMchId(), -orderDTO.getAmount(), orderDTO.getAmount());
            if (result == 0) {
                throw new PostResourceException("账户余额不正确");
            }
        }

        //更新用户冻结金额
       if (updateStatus ==4){
           int status = payAccountService.updateFreezeBalance(orderDTO.getMchId(), orderDTO.getAmount());
           if (status == 0) {
               throw new PostResourceException("账户冻结金额异常");
           }
       }


        settleOrder.setStatus(updateStatus);
        settleOrder.setUpdatedAt(LocalDateTime.now());
        updateById(settleOrder);

    }

    /**
     * 提交订单
     * @param inputDTO
     * @throws PostResourceException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void postOrder(SettleOrderInputDTO inputDTO) throws PostResourceException, ResourceNotFoundException {
        Merchant merchant = iMerchantService.getById(inputDTO.getMchId());
        String chinaRegex = "^[\\u4E00-\\u9FA5]+$";
        String bankRegex = "^([1-9]{1})\\d{15,}$";
        if (!inputDTO.getAccountNumber().matches(bankRegex)){
            throw new PostResourceException("银行账号错误");
        }
        if (!inputDTO.getAccountName().matches(chinaRegex)){
            throw new PostResourceException("账户名异常");
        }

        if (merchant == null) {
            throw new PostResourceException("商户不存在");
        }
        //商户状态 判断
        if (!merchant.getStatus()) {
            throw new PostResourceException("商户状态为禁用");
        }
        int result = payAccountService.updateBalance(inputDTO.getMchId(),inputDTO.getAmount(),-inputDTO.getAmount());
        if (result==0){
            // 账户yue不足
            throw new PostResourceException("账户余额不足");
        }

        //计算结算金额
        //获取对应商户的费率
        SettleAccountDTO settleAccountDTO = iSettleAccountService.findByMerchantId(merchant.getId());
        if (!settleAccountDTO.getStatus()) {
            throw new PostResourceException("结算状态已关闭");
        }
        Integer feeType = settleAccountDTO.getSettleFeeType();
        Integer orderFee;
        if (feeType == 1){
            // 当手续费类型为百分比收费时，根据订单金额计算手续费
            BigDecimal feeRate = settleAccountDTO.getSettleFeeRate();
            if (feeRate == null) throw new PostResourceException("结算失败，系统异常");
            orderFee = NumberUtil.calculateAmountFee(inputDTO.getAmount(),feeRate);
        }else if (feeType == 2){
            // 当手续费类型为固定收费时，手续费为固定金额
            Integer feeAmount = settleAccountDTO.getSettleFeeAmount();
            if (feeAmount == null) throw new PostResourceException("结算失败，系统异常");
            orderFee = feeAmount;
        }else if(feeType == 3){
            // 当手续费类型为百分比加固定收费时，根据订单金额计算手续费然后加固定手续费
            BigDecimal feeRate = settleAccountDTO.getSettleFeeRate();
            Integer feeAmount = settleAccountDTO.getSettleFeeAmount();
            if (feeRate == null||feeAmount == null) throw new PostResourceException("结算失败，系统异常");
            orderFee = NumberUtil.calculateAmountFee(inputDTO.getAmount(),feeRate);
            orderFee += feeAmount;
        }else {
            throw new PostResourceException("结算失败，系统异常");
        }

        //记录到订单中
        SettleOrder settleOrder = modelMapper.map(inputDTO, SettleOrder.class);
        settleOrder.setOrderNo(String.valueOf(idWorker.nextId()));
        settleOrder.setOrderSn(EncryptUtil.baseTimelineCode());
        settleOrder.setSettleType(true);
        settleOrder.setStatus(1);
        settleOrder.setFee(orderFee);
        settleOrder.setSettleAmount(settleOrder.getAmount()-orderFee);
        settleOrder.setCreatedAt(LocalDateTime.now());
        settleOrder.setUpdatedAt(settleOrder.getCreatedAt());
        save(settleOrder);

    }


    @Override
    public List<SettleOrder> selectSettlesToday() {
        return this.baseMapper.selectSettlesToday(new LambdaQueryWrapper<>());
    }

    @Override
    public HashMap<String,Object> findHomeDate() {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();



        //查询昨天0点到昨天当前时间总订单数
        Integer yestdayRealorderData = orderService.yestdayRealorderData();
        //查询今日0点到当前时间总订单数
        Integer intradayRealorderData = orderService.intradayRealorderData();
        //同比昨日
        String format = percentBigDecimal(new BigDecimal( intradayRealorderData), new BigDecimal(yestdayRealorderData));

        List<HashMap<String,Object>> statistics = new ArrayList<>();
        data.put("name", "今日收单笔数");
        data.put("val",intradayRealorderData);
        data.put("val2",format);
        data.put("rightHint", "日同比");
        data.put("leftHint","昨日");
        data.put("upDay",yestdayRealorderData);
        statistics.add(data);



        //查询当天成功订单总数
        Integer intradayOrderSucc = orderService.findIntradayOrderSucc();
        //查询昨天成功订单总数
        Integer yesterdayOrderSucc = orderService.findYesterdayOrderSucc();

        //转换率 订单总数
        BigDecimal a = new BigDecimal(intradayOrderSucc);
        BigDecimal b = new BigDecimal(yesterdayOrderSucc);
        String percent4Count = p.percentBigDecimal(a, b);


        data = new HashMap<>();
        data.put("name", "今日成交笔数");
        data.put("val", intradayOrderSucc);
        data.put("val2",percent4Count);
        data.put("rightHint", "日同比");
        data.put("leftHint","昨日");
        data.put("upDay",yesterdayOrderSucc);
        statistics.add(data);
        //end



        //昨日当时时间成交总额
        Long aLong = orderService.yestdayRealmoneyData();
        if (aLong == null){
            aLong = 0L;
        }
        //今日当时时间成交总额
        Long dayAmount = orderService.intradayRealoneyData();
        if (dayAmount == null){
            dayAmount = 0L;
        }
        a = new BigDecimal(dayAmount);
        b = new BigDecimal(aLong);
        String percent4Amount = p.percentBigDecimal(a, b);

        data = new HashMap<>();
        data.put("name", "今日成交总额");
        data.put("val", NumberUtil.amountFen2Yuan(dayAmount.intValue()));

        data.put("val2", percent4Amount);
        data.put("rightHint", "日同比");
        data.put("leftHint","昨日");
        data.put("upDay",aLong);
        statistics.add(data);
        //end


        //上周成交总额
        List<CartogramDTO> cartogramDTOS = orderService.upSevenDayCartogram();
        long upSevenSucAmount = cartogramDTOS.stream().mapToLong(CartogramDTO::getAmount).sum();
        a = new BigDecimal( aLong);
        b = new BigDecimal(upSevenSucAmount);
        String upServen = p.percentBigDecimal(a, b);
        data = new HashMap<>();
        data.put("name", "昨日成交总额");
        data.put("val", String.valueOf(NumberUtil.amountFen2Yuan(aLong.intValue())));
        data.put("val2", upServen);
        data.put("rightHint", "周同比");

        statistics.add(data);
        //end

        map.put("statistics", statistics);
        //--end

        data = new HashMap<>();
        List<CartogramPayDTO> cartogramPayDTOS = orderService.payOrders();
        data.put("payOrder", "支付产品排行");
        data.put("var", cartogramPayDTOS);
        map.put("payOrder", data);
        //--end


        data = new HashMap<>();
        StatisticDTO statisticDTO = sevenDaycartogram();
        data.put("name", "一周统计");
        data.put("val", statisticDTO);
        map.put("sevenDay", data);
        //--end

        //24小时交易金额
        data = new HashMap<>();
        List<CartogramDTO> hourAmount = orderService.hourData4amount();
        List<CartogramDTO> hours = new ArrayList<>();

        List<CartogramDTO> dtoList = transfer(hourAmount, 0,23);
        hourAmount.addAll(dtoList);
        Collections.sort(hourAmount);
        data.put("name", "交易趋势");
        data.put("val", hourAmount);
        map.put("orderAmount", data);
        //--end

        //24小交易趋势
        data = new HashMap<>();
        hourAmount = orderService.hourData4count();

        dtoList = transfer(hourAmount, 0,23);
        hourAmount.addAll(dtoList);
        Collections.sort(hourAmount);
        data.put("name", "订单数量");
        data.put("val", hourAmount);
        map.put("tradingTrends", data);
        //end

        //一周交易趋势
        data = new ManagedMap<>();
        List<CartogramDTO> cartogramDTOList = orderService.sevenDay4CountAndAmount();
        List<CartogramDTO> weekList = transfer(cartogramDTOList, 1,7);
        cartogramDTOList.addAll(weekList);
        Collections.sort(cartogramDTOList);
            cartogramDTOList.stream().forEach(cartogramDTO -> {
                int name = NumberUtils.toInt(cartogramDTO.getName());
                cartogramDTO.setName("星期" + (name <7 ? MoneyFormatUtil.formatFractionalPart(name) : "日"));
            });
        data.put("name", "本周交易趋势");
        data.put("val", cartogramDTOList);
        map.put("weekList", data);

        //一月交易趋势
        data = new ManagedMap<>();
        List<CartogramDTO> month4CountAndAmount = orderService.currentMonth4CountAndAmount();
        List<CartogramDTO> monthList = transfer(month4CountAndAmount,1, getCurrentMonthLastDay());
        month4CountAndAmount.addAll(monthList);
        Collections.sort(month4CountAndAmount);
        Calendar calendar = Calendar.getInstance();
        month4CountAndAmount.stream().forEach( cartogramDTO -> {
            String name = cartogramDTO.getName();
            name = (calendar.get(Calendar.MONTH) + 1) + "-" +name;
            cartogramDTO.setName(name);
        });
        data.put("name", "当月交易趋势");
        data.put("val", month4CountAndAmount);
        map.put("monthList", data);

        //转化率
        data = new ManagedMap<>();
        List<CartogramPayStatusVo> payStatusVos = orderService.PayStatuss();
        List<CartogramPayStatusVo> tmpList = new ArrayList<>();
        List<Integer> status = payStatusVos.stream().map(cartogramPayStatusVo -> cartogramPayStatusVo.getStatus()).collect(Collectors.toList());
        for (int i = -2; i < 4; i++) {
            if (i == 0) {
                continue;
            }
            if (status.contains(i)) {
                continue;
            }
            CartogramPayStatusVo cartogramPayStatusVo = new CartogramPayStatusVo();
            cartogramPayStatusVo.setCount(0);
            cartogramPayStatusVo.setStatus(i);
            tmpList.add(cartogramPayStatusVo);
        }
        payStatusVos.addAll(tmpList);
        int count =payStatusVos.size();
        data.put("name", "转化率");
        data.put("val", payStatusVos);
        data.put("count", count);
        map.put("precent", data);
        //--end


        return map;
    }


    private void addTime(List<CartogramDTO> cartograms){
        List<CartogramDTO> cartogramDTOList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

//        List<String> collect = cartograms.stream().map(time -> time.getName()).collect(Collectors.toList());
        List<CartogramDTO> times = new ArrayList<>();
//        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。

//        cal.setTime(new Date());
//            for (int i = 0; i < 6; i++) {
//                cal.add(Calendar.DATE, -1);//取当前日期的前一天.
//                String format = sdf.format(cal.getTime());
//                if (collect.contains(format)) {
//                    continue;
//                }
//                CartogramDTO cartogramDTO = new CartogramDTO();
//                cartogramDTO.setName(format);
//                cartogramDTO.setCount(0);
//                cartogramDTO.setAmount(0l);
//                times.add(cartogramDTO);
//            }
        for (int i = 6; i >=0; i--){
            long dayTime = System.currentTimeMillis() - ((1000 * 60 * 60 * 24) * (i));
            String time = sdf.format(dayTime);
            List<CartogramDTO> collect1 = cartograms.stream().filter(cartogramDTO ->
                cartogramDTO.getName().equals(time)
            ).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(collect1)) {
                CartogramDTO cartogramDTO = new CartogramDTO();
                cartogramDTO.setName(time);
                cartogramDTO.setCount(0);
                cartogramDTO.setAmount(0l);
                cartogramDTOList.add(cartogramDTO);
            }else {
                cartogramDTOList.add(collect1.get(0));

            }

        }

        cartograms.clear();
        cartograms.addAll(cartogramDTOList);
    }


    private List<CartogramDTO> transfer(List<CartogramDTO> sources,int start, int addNum) {
        List<String> collect = sources.stream().map(cartogramDTO -> cartogramDTO.getName()).collect(Collectors.toList());
        List<CartogramDTO> addData = new ArrayList<>();
        for (int i = start; i <= addNum; i++) {
            String time = String.valueOf(i);
            if (collect.contains(time)) {
                continue;
            }

            CartogramDTO cartogramDTO = new CartogramDTO();
            cartogramDTO.setName(time);
            cartogramDTO.setCount(0);
            cartogramDTO.setSuccessCount(0);
            cartogramDTO.setAmount(0l);
            cartogramDTO.setSuccessAmount(0l);
            addData.add(cartogramDTO);

        }

        return addData;
    }
    /**
     * 取得当月天数
     * */
    public static int getCurrentMonthLastDay()
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    @Override
    public StatisticDTO sevenDaycartogram(){
        StatisticDTO statisticDTO = new StatisticDTO();


        List<CartogramDTO> cartogram = orderService.sevenDayAllCount();
        List<CartogramDTO> cartogramDTOS = orderService.sevenDayAllAmount();


        addTime(cartogram);

        for (int i = 0; i < cartogram.size(); i++) {
            CartogramDTO cartogramDTO = cartogram.get(i);
            String name = cartogramDTO.getName();
            boolean finded = false;
            for (CartogramDTO carto : cartogramDTOS) {
                if (carto.getName()!=null && name.equals(carto.getName())) {
                    finded = true;
                    break;
                }
            }
            if (!finded) {
                CartogramDTO dto = new CartogramDTO();
                dto.setName(name);
                dto.setCount(0);
                dto.setAmount(0l);
                cartogramDTOS.add(i, dto);
            }

        }


        ArrayList<Map<String, Object>> list = new ArrayList<>();


        for (int i = 0; i < cartogram.size(); i++) {
            CartogramDTO allCount = cartogram.get(i);
            CartogramDTO allAmount = cartogramDTOS.get(i);

            HashMap<String, Object> map = new HashMap<>();

            map.put("time",allCount.getName());
            //收单总数
            List<HashMap<String, Object>> datas = new ArrayList<>();

            HashMap<String, Object> data = new HashMap<>();
            data.put("title", "收单笔数");
            data.put("data",allCount.getCount());
            datas.add(data);

            data = new HashMap<>();
            data.put("title", "成交笔数");
            data.put("data",allAmount.getCount());
            datas.add(data);

            data = new HashMap<>();
            data.put("title", "收单总额");
            data.put("data",allCount.getAmount());
            datas.add(data);

            data = new HashMap<>();
            data.put("title", "成交总额");
            data.put("data",allAmount.getAmount());
            datas.add(data);

            map.put("data", datas);

            list.add(map);


        }
        statisticDTO.setData(list);
        return statisticDTO;
    }

    private  PercentCount p = new PercentCount();
    private String percentBigDecimal(BigDecimal total,BigDecimal num){

        String percent = p.percentBigDecimal(total,num);
        return percent;
    }
}
