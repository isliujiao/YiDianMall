package com.atguigu.gulimall.cart.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-10-31-19:52
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    //根据skuId查询商品属性
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuinfo(@PathVariable("skuId") Long skuId);

    //查询sku属性的组合信息
    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    //根据id查询价格
    @GetMapping("/product/skuinfo/{skuId}/pice")
    R getPrice(@PathVariable("skuId") Long skuId);
}
