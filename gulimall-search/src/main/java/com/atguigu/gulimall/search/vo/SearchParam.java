package com.atguigu.gulimall.search.vo;

import lombok.Data;
import org.elasticsearch.common.util.IntArray;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 *
 * 参数：
 * catalog3Id=225&keyword&sort=saleCont_ass&hasStock=0/1
 *
 * @author:厚积薄发
 * @create:2022-10-16-16:07
 */
@Data
public class SearchParam {

    //页面传递过来的全文匹配关键字
    private String keyword;

    //三级分类ID
    private Long catalog3Id;

    /**
     * 排序条件
     *  sort=salecount_asc/
     *  descsort=skuPrice_asc/
     *  descsort=hotScore_asc / desc
     */
    private String sort;

    /**
     * 好多的过滤条件
     * hasStock(是否有货)、skuPrice区间、brandId、 cataLog3Id、 attrs
     * hasStock=0/1
     * skuPrice=1_500 / _500 / 500_
     * brandId=1
     * attrs=2_5寸:6寸
     */
    //是否只显示有货 —— 0（无库存）、1（有库存，默认）
    private Integer hasStock;
    //价格区间查询
    private String skuPrice;
    //按照品牌进行查询，可以多选
    private List<Long> brandId;
    //按照属性进行筛选
    private List<String> attrs;

    //页码
    private Integer pageNum = 1;

    //原生的所有查询条件（从页面传来）
    private String _queryString;

}
