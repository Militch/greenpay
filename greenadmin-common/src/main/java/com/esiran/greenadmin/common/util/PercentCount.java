package com.esiran.greenadmin.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author han
 * @Package com.esiran.greenpay.common.util
 * @date 2020/5/25 14:53
 */
public  class PercentCount  {

    public String percentBigDecimal(BigDecimal preNum,BigDecimal sufNum){
        double result = countDecimal(preNum,sufNum);
        if(result>0){
            return "+"+result+"%";
        }
        if(result<0){
            return result+"%";
        }
        if(result==0){
            return "+"+0+"%";
        }
        return null;
    }
    public  double countDecimal(BigDecimal preNum,BigDecimal sufNum){
        boolean preBoolean = verifyNum(preNum);
        boolean sufBoolean = verifyNum(sufNum);
        //同时为true计算
        if(preBoolean && sufBoolean){
            boolean b = verifyEqual(preNum, sufNum);
            if (b == false){
                return realCountDecimal(preNum,sufNum);
            }
            if (b){
                return 0;
            }
        }
        if(preBoolean == false && sufBoolean ==false){
            return 0;
        }
        if(sufBoolean ==false){
            return 100;
        }
        return  0;
    }
    //验证数字是否为零和null
    public boolean verifyNum(BigDecimal num){
        if(null !=num && num.compareTo(BigDecimal.ZERO)!=0 ){
            return true;
        }
        return false;
    }

    //验证两个数字是否相等
    public boolean verifyEqual(BigDecimal preNum,BigDecimal sufNum){
        int n = preNum.compareTo(sufNum);
        //比较 -1 小于   0 等于    1 大于
        if(n==0){
            return true;
        }
        return false;
    }
    //真正计算
    public double realCountDecimal(BigDecimal preNum,BigDecimal sufNum){
        //(前面的数字-后面的数字)/后面的数字*100
        BigDecimal bigDecimal = (preNum.subtract(sufNum)).divide(sufNum,2, RoundingMode.HALF_UP).multiply(new BigDecimal("1")).setScale(2, BigDecimal.ROUND_UP);
        if (bigDecimal.compareTo(BigDecimal.ZERO) !=0){
            return  bigDecimal.doubleValue();
        }
        return 0;
    }

    public static void main(String[] args) {
        PercentCount p = new PercentCount();
        BigDecimal a = new BigDecimal("0");
        BigDecimal b = new BigDecimal("0.2");
        String percent = p.percentBigDecimal(a, b);
        System.out.println(percent);
    }

}