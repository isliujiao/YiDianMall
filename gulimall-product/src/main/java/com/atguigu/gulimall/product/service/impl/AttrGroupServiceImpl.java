package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    //获取分类属性分组
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) {//如果没有catelogId就查出所有数据
            IPage<AttrGroupEntity> page =
                    this.page(new Query<AttrGroupEntity>().getPage(params),
                            new QueryWrapper<AttrGroupEntity>());
            return new PageUtils(page);
        } else {//如果有catelogId,就按三级分类
            String key = (String) params.get("key");//如果检索条件不是空，则用检索条件查询出模糊信息-->分组id、组名
            //select * from pms_attr_group where catelog_id = ? and (attr_group_id = key or attr_group_name = %key%)
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
            wrapper.eq("catelog_id", catelogId);//匹配分组id
            if (!StringUtils.isEmpty(key)) {//判断key是否存在，如果非空即存在，则进行模糊查询
                wrapper.and((obj) -> {
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key);
                });
            }
            IPage<AttrGroupEntity> page =
                    this.page(new Query<AttrGroupEntity>().getPage(params),
                            wrapper);

            return new PageUtils(page);
        }
    }

}