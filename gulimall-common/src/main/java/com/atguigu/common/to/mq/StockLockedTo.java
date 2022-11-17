package com.atguigu.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-11-08-14:22
 */
@Data
public class StockLockedTo {

    private Long id;//库存工作单的id

    private StockDetailTo detailTo;//工作详情的id

}
