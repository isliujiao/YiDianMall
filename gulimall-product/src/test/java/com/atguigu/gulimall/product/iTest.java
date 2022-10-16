package com.atguigu.gulimall.product;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author:厚积薄发
 * @create:2022-09-12-19:18
 */
@Slf4j
public class iTest {


    @Test
    public void tt(){
        String classId = String.valueOf("123");
        classId = "%" + classId + "%";
        System.out.println(classId);
    }

    @Test
    public void t() {
        System.out.println(isFlipedString("waterbottle", "erbottlewat"));
    }


    public boolean isFlipedString(String s1, String s2) {

        return s1.length() == s2.length() && (s1 + s1).indexOf(s2) != -1;
    }
}