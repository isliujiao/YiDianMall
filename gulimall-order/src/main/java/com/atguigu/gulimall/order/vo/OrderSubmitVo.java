package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交的数据
 * @author:厚积薄发
 * @create:2022-11-04-12:02
 */
@Data
public class OrderSubmitVo {

    private Long addrId;//收货地址的id
    private Integer payType;//支付方式
    private String orderToken;//防重令牌
    private BigDecimal payPrice;//应付价格 验价
    private String note;//订单备注

    //无需提交需要购买的商品，去购物车再获取一遍
    //优惠、发票
    //用户相关信息，直接去session取出登录用户

}
