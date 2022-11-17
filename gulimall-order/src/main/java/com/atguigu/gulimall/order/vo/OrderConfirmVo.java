package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单页面需要展示的数据
 *
 * @author:厚积薄发
 * @create:2022-11-03-9:19
 */
public class OrderConfirmVo {

    //收货地址列表 -- ums_member_receive_address表
    @Setter @Getter
    List<MemberAddressVo> address;

    //所有选中的购物项
    @Setter @Getter
    List<OrderItemVo> items;

    //发票记录……

    //优惠券信息……

    //积分
    @Setter @Getter
    Integer integration;

    //库存信息
    @Setter @Getter
    Map<Long,Boolean> stocks;

    //防重令牌（防止重复提交）
    @Setter @Getter
    String orderToken;

    //订单总额
    BigDecimal total;

    //应付价格
    BigDecimal payPrice;

    public Integer getCount(){
        Integer count = 0;
        if(items != null){
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal(0);
        if(items != null){
            for (OrderItemVo item : items) {
                BigDecimal itemPrices = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(itemPrices);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
