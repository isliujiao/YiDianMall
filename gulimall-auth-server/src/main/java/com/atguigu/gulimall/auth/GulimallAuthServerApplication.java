package com.atguigu.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * SpringSession核心原理：
 *   1、@EnableRedisHttpSession 导入 RedisHttpSessionConfiguration配置
 *      1）、该配置给容器中添加了一个组件`RedisOperationsSessionRepository`
 *          SessionRepository --> RedisOperationsSessionRepository --> redis操作session的增删改查
 *      2）、继承 SessionRepositoryFilter ==> Filter：session存储过滤器；每一个请求过来都必须经过filter
 *              ①创建的时候，就自动从容器中获取到了 SessionRepository
 *              ②原生的request、response都被包装。--> SessionRepositoryRequestWrapper、SessionRepositoryResponseWrapper
 *              ③以后获取session。request.getSession();（被重写）
 *              ④wrappedRequset.getSession(); ---> SessionRepository中获取到的
 *  装饰着模式
 *  自动延期。redis中的数据也有过期时间
 */

@EnableRedisHttpSession //整合Redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
