package com.esiran.greenpay.pay.entity;

import com.esiran.greenpay.common.util.NumberUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author han
 * @Package com.esiran.greenpay.pay.entity
 * @date 2020/5/23 18:11
 */
@Data
public class CartogramDTO implements Comparable<CartogramDTO> {

    private String name;

    private Date time;

    private Integer count;
    private Integer successCount;

    private Long amount;
    private Long successAmount;

    @Override
    public int compareTo(@NotNull CartogramDTO o) {
//        int i = this.name.compareTo(o.getName());

        int a = Integer.valueOf(name).intValue();
        int b = Integer.valueOf(o.getName()).intValue();
        return a- b;
    }
}
