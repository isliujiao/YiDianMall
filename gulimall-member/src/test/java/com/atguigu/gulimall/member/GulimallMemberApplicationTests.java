package com.atguigu.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void cont(){

        //e10adc3949ba59abbe56e057f20f883e
        //抗修改性：彩虹表。123456 ——> xxxxx;暴力破解
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);

        //MD5不能直接进行密码的加密存储；
        //盐值加密;随机值
        String s1 = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println(s1);

        //
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");

        boolean matches = passwordEncoder.matches("123456", "$2a$10$YCO/ln8tMTE0HXv4Lc7qbeBghqF8YLJMZ3m/lrO0Etu7IaqfLaF0W");
        System.out.println(encode + "----->" + matches);

    }


}
