package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author:厚积薄发
 * @create:2022-09-19-21:14
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 查询商品信息
     * 1)、让所有请求过网关：
     *      1、@FeignClient("gulimall-gateway"):给gulimall-gateway所在的机器发请求
     *      2、api/product/skuinfo/info/{skuId}
     *  2)、直接让后台指定服务处理
     *      1、@FeignClient("gulimall-gateway")
     *      2、/product/skuinfo/info/{skuId}
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

}
