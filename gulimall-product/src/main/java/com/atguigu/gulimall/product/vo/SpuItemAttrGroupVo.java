package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-10-24-21:19
 */
@Data
@ToString
//spu规格参数信息
public class SpuItemAttrGroupVo {

    private String groupName;
    private List<Attr> attrs;

}
