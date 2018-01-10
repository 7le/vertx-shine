package top.arkstack.shine.web.annotations;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;
/**
 * 通过路径扫描订阅 guava eventbus
 *
 * @author : 7le
 * @since v1.0.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface EventSubscriber {
}
