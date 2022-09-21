package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @author:厚积薄发
 * @create:2022-09-13-16:39
 */
@Data
public class AttrRespVo extends AttrVo{

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;


}
