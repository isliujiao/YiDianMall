package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @author:厚积薄发
 * @create:2022-09-13-16:39
 */
@Data
public class AttrRespVo extends AttrVo{

    //三级分类所属分类名字
    private String catelogName;

    //所属分组名字
    private String groupName;

    //三级分类路径
    private Long[] catelogPath;
}
