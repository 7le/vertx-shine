package top.arkstack.shine.web.annotations;

import java.lang.annotation.*;

/**
 * 用于部署Verticle handler
 *
 * @author 7le
 * @since v1.0.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Verticle {

    String value() default "";

    /**
     * 是否启动集群模式
     */
    boolean isCluster() default true;
}
