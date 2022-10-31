package com.atguigu.gulimall.member.service.impl;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    //用户注册
    @Override
    public void regist(MemberRegistVo vo) {
        MemberDao mapperDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();
        MemberLevelEntity levelEntity = mapperDao.getDefaultLevel();

        entity.setLevelId(levelEntity.getId());

        //检查用户名和手机号是否唯一.为了让controller感知异常，使用异常机制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());

        //密码要进行加密存储,使用BCryptPasswordEncoder（spring带的盐值加密）
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);

        mapperDao.insert(entity);
    }

    //校验邮箱是否唯一
    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        MemberDao mapperDao = this.baseMapper;
        Integer mobile = mapperDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
    }

    //校验用户名是否唯一
    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException {
        MemberDao mapperDao = this.baseMapper;
        Integer count = mapperDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0) {
            throw new UserNameExistException();
        }
    }

    //登录
    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //1、去数据库查询
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if (entity == null) {
            //登录失败
            return null;
        } else {
            //1、获取数据库的password
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //2、matches(明文密码，数据库加密后密码)进行校验
            boolean matches = passwordEncoder.matches(password, passwordDb);
            if (matches) {
                return entity;
            }
            return null;
        }
    }

    //社交登录(登录过更新登录信息、未登录过就录入信息)
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        //登录和注册合并逻辑
        String uid = socialUser.getUid();
        //1、判断当前社交用户是否已经登录过系统
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            //这个用户已经注册(更新信息)。。。memberEntity返回数据、update 修改数据库
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccessToken());
            update.setExpiresIn(socialUser.getExpiresIn());

            memberDao.updateById(update);
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录，创键注册
            MemberEntity regist = new MemberEntity();
            try {
                //查询当前社交用户的社交帐号信息（昵称、性别等）
                Map<String, String> query = new HashMap<>();
                query.put("access_token", socialUser.getAccessToken());
                query.put("uid", socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    regist.setNickname(name);
                    regist.setGender("m".equals(gender) ? 1 : 0);
                    //.....等参数
                }
            } catch (Exception e) {
            }
            regist.setSocialUid(socialUser.getUid());
            regist.setAccessToken(socialUser.getAccessToken());
            regist.setExpiresIn(socialUser.getExpiresIn());
            memberDao.insert(regist);
            return regist;
        }
    }

}