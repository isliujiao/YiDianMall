package com.atguigu.gulimall.member.exception;

/**
 * @author:厚积薄发
 * @create:2022-10-27-11:02
 */
public class UserNameExistException extends RuntimeException{

    public UserNameExistException() {
        super("用户名不存在");
    }
}
