package com.atguigu.gulimall.member.exception;

/**
 * @author:厚积薄发
 * @create:2022-10-27-11:02
 */
public class PhoneExistException extends RuntimeException{

    public PhoneExistException() {
        super("手机号不存在");
    }
}
