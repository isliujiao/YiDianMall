package com.atguigu.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import java.lang.annotation.*;

/**
 * @author:厚积薄发
 * @create:2022-09-10-16:13
 */
@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class})//指定校验器
//这个注解可以标注在哪些位置（method:方法、属性、 、构造器、参数）
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME) //校验注解的时机，RUNTIME：可以在运行时获取到
public @interface ListValue {

    //出错以后错误信息去哪取（默认去配置文件取）
    String message() default "{com.atguigu.common.valid.ListValue.message}";

    //支持分组功能
    Class<?>[] groups() default {};

    //自定义负载均衡
    Class<? extends Payload>[] payload() default {};

    int[] vals() default {}; //默认没有的值

}
