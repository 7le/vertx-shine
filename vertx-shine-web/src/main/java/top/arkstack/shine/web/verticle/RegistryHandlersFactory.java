package top.arkstack.shine.web.verticle;

import com.google.common.base.Strings;
import io.vertx.core.DeploymentOptions;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.arkstack.shine.web.annotations.Verticle;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * 处理器注册 Factory
 *
 * @author 7le
 * @since v1.0.0
 */
public class RegistryHandlersFactory {

    private static Logger log = LoggerFactory.getLogger(RegistryHandlersFactory.class);

    /**
     * 需要扫描注册的Router路径
     */
    private static volatile Reflections reflections;

    /**
     * 默认路径前缀
     */
    public static volatile String BASE_ROUTER = "/";


    public RegistryHandlersFactory(String handlerScanAddress, String appPrefix) {
        Objects.requireNonNull(handlerScanAddress, "The router package address scan is empty.");
        reflections = new Reflections(handlerScanAddress);
        BASE_ROUTER = appPrefix;
    }

    public RegistryHandlersFactory(String handlerScanAddress) {
        Objects.requireNonNull(handlerScanAddress, "The router package address scan is empty.");
        reflections = new Reflections(handlerScanAddress);
    }

    public void registerVerticle() {
        log.info("Register Service Verticle...");
        Set<Class<?>> verticles = reflections.getTypesAnnotatedWith(Verticle.class);

        for (Class<?> handler : verticles) {
            if (handler.isAnnotationPresent(Verticle.class)) {
                VerticleLauncher.getStandardVertx().deployVerticle(handler.getName());
            }
        }
    }
}
