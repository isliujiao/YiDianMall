package com.atguigu.gulimall.product;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author:厚积薄发
 * @create:2022-09-12-19:18
 */
@Slf4j
public class iTest {


    @Test
    public void t12(){

    }


    @Test
    public void date() throws ParseException {
        String bir = "Sun Oct 16 00:00:00 CST 2022";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);//要转换的时间格式
        Date parse = sdf.parse(bir);

        System.out.println(parse);
    }

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