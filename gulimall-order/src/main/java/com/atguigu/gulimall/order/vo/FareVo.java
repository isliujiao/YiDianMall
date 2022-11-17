package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author:厚积薄发
 * @create:2022-11-04-8:52
 */
@Data
public class FareVo {

    private MemberAddressVo address;
    private BigDecimal fare;

}
