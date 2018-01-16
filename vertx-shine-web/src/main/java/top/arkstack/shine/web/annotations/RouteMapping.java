package top.arkstack.shine.web.annotations;

import java.lang.annotation.*;

/**
 * 将Web请求映射到特定的处理程序类或处理程序方法上
 * 类似 springmvc 中的@RequestMapping
 *
 * @author : 7le
 * @since v1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouteMapping {

    String value() default "";

    /**
     * 是否覆盖
     */
    boolean isCover() default true;

    /**
     * http method
     */
    RequestMethod[] method() default {};

    /**
     * 详情
     */
    String description() default "";

}
