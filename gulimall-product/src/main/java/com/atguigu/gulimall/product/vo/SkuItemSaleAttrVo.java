package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-10-24-21:21
 */
@Data
@ToString
//spu的销售属性
public class SkuItemSaleAttrVo {

    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;

}
