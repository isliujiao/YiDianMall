package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-11-03-14:06
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {

    //获取用户购物车购物项
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();

}
