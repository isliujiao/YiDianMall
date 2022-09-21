package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-09-19-11:00
 */
@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
