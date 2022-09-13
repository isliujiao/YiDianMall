package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author isliujiao
 * @email isliujiao@163.com
 * @date 2022-09-03 16:59:49
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
