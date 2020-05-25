package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StatisticDTO {


    private List<Map<String,Object>> data;
}
