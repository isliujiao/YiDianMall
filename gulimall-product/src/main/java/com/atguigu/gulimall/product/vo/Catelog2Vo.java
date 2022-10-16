package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-09-28-20:45
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
//2级分类
public class Catelog2Vo  {

    private String catalog1Id; // 1级分类id

    private List<Catelog3Vo> catalog3List;  // 三级分类

    private String id;

    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    // 3级分类
    public static class Catelog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }

}
