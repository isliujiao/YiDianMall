package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author:厚积薄发
 * @create:2022-09-25-20:46
 */

public interface ProductSaveService {
    /**
     * 商品上架
     * @param skuEsModels
     * @return
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
