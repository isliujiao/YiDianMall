package com.atguigu.gulimall.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用RabbitMQ
 * 1、引入场景启动器：RabbitAutoConfiguration 自动生效
 * 2、给容器中自动配置了：
 *      RabbitTemplate(操作消息)、AmqpAdmin(创建组件)、
 *      CachingConnectionFactory、RabbitMessagingTemplate
 *      所有属性配置都是：
 *      @ConfigurationProperties(prefix = "spring.rabbitmq")
 *      public class RabbitProperties
 *
 * 3、给配置文件中配置 spring.rabbitmq……信息
 * 4、@EnableRabbit 开启RabbitMQ
 * 5、监听消息，使用@RabbitListener；必须有 @EnableRabbit
 *      @RabbitListener：类+方法上（监听哪些队列）
 *      @RabbitHandler: 方法上（可以重载区分不同的消息）
 * 6、确认回调：
 *      1）、spring.rabbitmq.publisher-confirms=true
 *      2）、rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
 * 二、本地事务失效问题
 *      同一个对象内事务方法互调默认失效，原因：绕过了代理对象，事务使用代理对象来控制
 *      解决：使用代理对象来调用事务方法
 *      1）、引入aop-starter(aspectjweaver)
 *      2)、@EnableAspectJAutoProxy 开始 aspectj 动态代理功能，以后所有动态代理都是aspectj创建的（即使没有接口也可以创建动态代理）
 *              exposeProxy = true：对外暴露代理对象
 *      3）、本类互调调用对象
 *      代码………
 * 三、Seata控制分布式事务
 *      1）、每一个微服务必须先创建undo_log表;
 *      2）、安装事务协调器：seata-server
 *      3）、整合Seata
 *          1、导入依赖 spring-cloud-starter-alibaba-seata、seata-all:0.7.1
 *                  --（注意seata版本控制：<version>1.2.0</version> -- alibaba.cloud2.1.0.RELEASE）
 *          2、启动seata-server服务器
 *                  registry.conf：注册中心配置（修改registry type=nacos）
 *                  file.conf：配置文件
 *
 *          3、所有想用到分布式事务的微服务使用seata DataSourceProxy代理自己的数据源
 *          4、每个微服务，都必须导入：
 *                  registry.conf
 *                  file.conf：  vgroup_mapping.【application.name】-fescar-service-group = "default"
 *          5、启动测试分布式事务
 *          6、给分布式大事务的入口标注：@GlobalTransactional（全局分布式事务注解）
 *          7、每一个远程的小事务用 @Transactional 即可
 *
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableRabbit
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
