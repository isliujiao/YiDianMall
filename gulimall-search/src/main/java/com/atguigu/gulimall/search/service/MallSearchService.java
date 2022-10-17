package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * @author:厚积薄发
 * @create:2022-10-16-16:12
 */
public interface MallSearchService {
    /**
     *
     * @param searchParam 检索的所有参数
     * @return 检索的结果 包含页面需要的所有信息
     */
    SearchResult search(SearchParam searchParam);
}
