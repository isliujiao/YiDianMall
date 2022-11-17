package com.atguigu.guigumall.coupon;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GulimallCouponApplicationTests {

    @Test
    public void contextLoads() {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);//now + 2天
        LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
        String end = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(end);
    }

}
