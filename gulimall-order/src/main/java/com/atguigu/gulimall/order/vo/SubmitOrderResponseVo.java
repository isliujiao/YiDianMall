package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author:厚积薄发
 * @create:2022-11-04-19:49
 */
@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;
    private Integer code;//错误状态码 0成功

}
