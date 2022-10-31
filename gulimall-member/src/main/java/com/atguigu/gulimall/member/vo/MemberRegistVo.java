package com.atguigu.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

/**
 * @author:厚积薄发
 * @create:2022-10-27-10:07
 */
@Data
public class MemberRegistVo {
    private String userName;

    private String password;

    private String phone;
}
