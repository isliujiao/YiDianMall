package com.atguigu.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 想要远程调用的步骤
 *      1.引入open-feign
 *      2.编写一个接口，告诉springcloud这个接口需要调用远程服务(@FeignClient)
 *          1>声明接口的每一个方法都是调用哪个远程服务的哪个请求
 *      3.开启远程调用功能：@FeignClient
 *      4.@EnableFeignClients进行扫描feign在当前服务的位置
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.atguigu.gulimall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
