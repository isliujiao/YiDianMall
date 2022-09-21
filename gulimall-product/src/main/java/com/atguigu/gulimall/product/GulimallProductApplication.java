package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 * @ControllerAdvice
 */
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@MapperScan("com.atguigu.gulimall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
