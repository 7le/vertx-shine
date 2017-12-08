package top.arkstack.shine.web.verticle;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author 7le
 * @Description: 处理器注册 Factory
 * @date 2017年12月7日
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

    public void registerVerticle(){
        log.info("Register Service Verticle...");
    }
}
