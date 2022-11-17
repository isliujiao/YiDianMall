package com.atguigu.gulimall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 传输对象to
 * @author:厚积薄发
 * @create:2022-10-31-16:32
 */
@ToString
@Data
public class UserInfoTo {

    private Long userId;
    private String userKey; // 一定封装

    //是否获取过临时用户
    private boolean tempUser = false;

}
