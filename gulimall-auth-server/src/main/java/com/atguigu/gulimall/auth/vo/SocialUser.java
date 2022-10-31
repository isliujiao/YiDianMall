package com.atguigu.gulimall.auth.vo;

import lombok.Data;

/**
 * @author:厚积薄发
 * @create:2022-10-28-15:05
 */
@Data
public class SocialUser {

    private String accessToken;
    private String remindIn;
    private long expiresIn;
    private String uid;
    private String isRealName;

}
