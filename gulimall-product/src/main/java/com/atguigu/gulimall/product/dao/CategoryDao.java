package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author isliujiao
 * @email isliujiao@163.com
 * @date 2022-09-03 10:05:32
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
