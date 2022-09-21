package com.atguigu.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author:厚积薄发
 * @create:2022-09-17-14:11
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallTestApplication {
    public static void main(String[] args){
        SpringApplication.run(GulimallTestApplication.class,args);
    }

}
