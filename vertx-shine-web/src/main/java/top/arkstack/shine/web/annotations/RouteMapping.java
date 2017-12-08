package top.arkstack.shine.web.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
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
    RequestMethod method() default RequestMethod.GET;

    /**
     * 详情
     */
    String description() default "";

}
