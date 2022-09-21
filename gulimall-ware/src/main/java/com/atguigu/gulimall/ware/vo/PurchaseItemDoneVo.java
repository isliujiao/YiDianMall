package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author:厚积薄发
 * @create:2022-09-19-19:42
 */
@Data
public class PurchaseItemDoneVo {

    private Long itemId;

    private Integer status;

    private String reason;

}
