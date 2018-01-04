package top.arkstack.shine.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 通过路径扫描注册
 * 与 {@link top.arkstack.shine.web.annotations.RouteMapping} 共用
 * 类似 springmvc 中的@Controller
 *
 * @author : 7le
 * @since v1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteHandler {

    String value() default "";
}
