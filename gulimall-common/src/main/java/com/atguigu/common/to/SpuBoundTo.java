package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author:厚积薄发
 * @create:2022-09-17-14:43
 */
@Data
public class SpuBoundTo {

    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;

}
