package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author:厚积薄发
 * @create:2022-11-05-16:48
 */
@Data
public class LockStockResult {

    private Long skuId;

    private Integer num;//数量

    private Boolean locked;//是否锁住

}
