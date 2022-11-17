package com.atguigu.gulimall.order.to;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-11-05-11:05
 */
@Data
public class OrderCreateTo {

    //订单实体类
    private OrderEntity order;

    //每一个订单项
    private List<OrderItemEntity> orderItems;

    //订单应付价格
    private BigDecimal payPrice;

    //运费
    private BigDecimal fare;
}
