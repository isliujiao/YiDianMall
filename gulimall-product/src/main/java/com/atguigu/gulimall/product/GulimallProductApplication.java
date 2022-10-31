package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1、整合mybatis-plus
 *  1）导入依赖
 *      <dependency>
*           <groupId>com.baomidou</groupId>
 *          <artifactId>mybatis-plus-boot-starter</artifactId>
 *          <version>3.2.0</version>
 *      </dependency>
 *  2）配置
 *      1、配置数据源
 *          1>导入数据库驱动
 *          2>application.yml配置数据源相关信息
 *      2、配置mybatis-plus；
 *          1>使用@MapperScan扫描
 *          2>告诉mybatis-plus，sql映射文件的位置（application.yml），并统一设置主键自增
 *  3）JSR303
 *      1)、给Bean添加校验注解：javax.validation.constraints,定于自己的message信息
 *      2)、开启校验功能，在需要校验控制类传输参数上 @Valid
 *        效果：校验错误以后有默认的响应
 *      3)、紧跟BindingResult,就可以获取校验的结果
 *      4)、分组校验功能
 *          1> @NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})	//必须包含非空字符
 *             给校验注解标注什么时候需要进行校验
 *          2> @Validated({AddGroup.class})
 *             在Controller类 标注使用哪组校验
 *          3> 默认没有指定分组校验注解在分组校验情况下不生效 ！
 *      5)、自定义检验
 *          1>编写一个自定义检验注解
 *          2>编写一个自定义检验器    ConstraintValidator
 *          3>关联自定义的校验注解和检验器
     *          @Documented
     *          @Constraint(validatedBy = {ListValueConstraintValidator.class,【可以指定多个不同的校验器】})
     *          @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
     *          @Retention(RetentionPolicy.RUNTIME)
     *          public @interface ListValue {
 *  4）统一异常处理
 * @CibtrollerAdvice
 *  1)、编写异常处理类，使用@CibtrollerAdvice
 *  2)、使用@ExceptionHandler标注方法可以处理的异常
 *
 *  5、模板引擎
 *      1)、引入thymeleaf-starter，关闭缓存
 *      2)、静态资源都泛着该static文件夹下就可以按照路径直接访问
 *      3)、页面放在templates下，直接访问（Springboot默认访问index）
 *      4)、页面修改不重启服务器实时更新
 *          1> 引入dev-tools
 *          2> 修改完页面 shift F9 --> build project重新构建
 *
 *  6、整合Redis
 *   1)、引入data-redis-starter
 *   2)、简单配置redis的host等信息
 *   3)、使用SpringBoot自动配置好的StringRedisTemplate来操作redis
 *      redis --> Map(Map相当于本地单线程缓存，redis可以分布式多线程缓存)
 *
 *  7、整合redisson作为分布式锁等功能框架
 *   1）、引入依赖
 *          <dependency>
 *             <groupId>org.redisson</groupId>
 *             <artifactId>redisson</artifactId>
 *             <version>3.12.0</version>
 *         </dependency>
 *   2）、配置redisson
 *      MyRedissonConfig给容器中配置一个RedissonClient实例即可
 *   3)、使用
 *      参照文档做
 *
 *  8、整合SpringCache简化缓存开发
 *      1)、引入依赖
 *            <artifactId>spring-boot-starter-cache</artifactId>
 *      2)、写配置哦
 *          (1)、自动配置了哪些
 *              CacheAuroConfiguration会导入RedisCacheConfiguration
 *              自动配置好了缓存管理器RedisCacheManager
 *          (2)、配置使用redis作为缓存：spring.cache.type=redis
 *      3)、测试使用缓存
 *              @Cacheable：触发将数据保存到缓存的操作
 *              @CacheEvict：出发将数据从缓存删除的操作
 *              @CachePut： 不影响方法执行更新缓存
 *              @Caching：组合以上多个操作
 *              @CacheConfig：在类级别共享缓存的相同配置
 *              1)、开启缓存功能 @EnableCaching
 *              2)、只需要使用注解就能完成缓存操作
 *      4)、原理：
 *          CacheAutoConfiguration ->RedisCacheConfiguration ->
 *          自动配置了RedisCacheManager->初始化所有的缓存->每个缓存决定使用什么配置->
 *          ->如果redisCacheConfiguration有就用已有的，没有就用默认配置
 *          ->想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可-
 *          >就会应用到当前RedisCacheManager管理的所有缓存分区中
 *
 */

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@MapperScan("com.atguigu.gulimall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
