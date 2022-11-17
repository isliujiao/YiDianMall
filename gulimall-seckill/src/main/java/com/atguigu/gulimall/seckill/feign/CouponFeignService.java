package com.atguigu.gulimall.seckill.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author:厚积薄发
 * @create:2022-11-11-19:34
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    //查询最近3天的秒杀商品
    @GetMapping("/coupon/seckillsession/lates3DaySession")
    R getLates3DaySession();

}
