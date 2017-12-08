package top.arkstack.shine.web.verticle;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.reflections.Reflections;
import top.arkstack.shine.web.annotations.RequestMethod;
import top.arkstack.shine.web.annotations.RouteHandler;
import top.arkstack.shine.web.annotations.RouteMapping;
import top.arkstack.shine.web.vertx.VertxRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author 7le
 * @Description: Router Factory
 * @date 2017年12月7日
 * @since v1.0.0
 */
public class RouterHandlerFactory {

    private static Logger log = LoggerFactory.getLogger(RouterHandlerFactory.class);

    /**
     * 需要扫描注册的Router路径
     */
    private static volatile Reflections reflections;

    /**
     * 默认api前缀
     */
    public static volatile String GATEWAY_PREFIX = "/";

    public RouterHandlerFactory(String routerScanAddress, String gatewayPrefix) {
        Objects.requireNonNull(routerScanAddress, "The router package address scan is empty.");
        reflections = new Reflections(routerScanAddress);
        GATEWAY_PREFIX = gatewayPrefix;
    }

    public RouterHandlerFactory(String routerScanAddress) {
        Objects.requireNonNull(routerScanAddress, "The router package address scan is empty.");
        reflections = new Reflections(routerScanAddress);
    }

    /**
     * 扫描注册handler
     */
    public Router createRouter() {
        Router router = VertxRouter.getRouter();
        router.route("/*").handler(BodyHandler.create()).handler(CookieHandler.create());
        //设置跨域
        Set<HttpMethod> method = new HashSet<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.POST);
            add(HttpMethod.OPTIONS);
            add(HttpMethod.PUT);
            add(HttpMethod.DELETE);
            add(HttpMethod.HEAD);
        }};
        router.route().handler(CorsHandler.create("*").allowedMethods(method));
        Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouteHandler.class);
        try {
            handlers.stream().forEach(handler -> {
                try {
                    registerHandler(router, handler);
                } catch (Exception e) {
                    log.error("Error register {} ", handler, e);
                }
            });
        } catch (Exception e) {
            log.error("Register handlers fail ", e);
        }
        return router;
    }

    private void registerHandler(Router router, Class<?> handler) throws Exception {
        String root = GATEWAY_PREFIX;
        if (!root.startsWith("/")) {
            root = "/" + root;
        }
        if (handler.isAnnotationPresent(RouteHandler.class)) {
            RouteHandler routeHandler = handler.getAnnotation(RouteHandler.class);
            root = root + routeHandler.value();
        }
        Object instance = handler.newInstance();
        Method[] methods = handler.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(RouteMapping.class)) {
                RouteMapping mapping = method.getAnnotation(RouteMapping.class);
                RequestMethod requestMethod = mapping.method();
                String routeUrl;
                if (mapping.value().startsWith("/:")) {
                    routeUrl = (method.getName() + mapping.value());
                } else {
                    routeUrl = (mapping.value().endsWith(method.getName()) ? mapping.value() : (mapping.isCover() ? mapping.value() : mapping.value() + method.getName()));
                    if (routeUrl.startsWith("/")) {
                        routeUrl = routeUrl.substring(1);
                    }
                }
                String url = root.concat("/" + routeUrl);
                Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) method.invoke(instance);
                log.info("Register New Handler -> {}:{}", requestMethod, url);
                switch (requestMethod) {
                    case ROUTE:
                        router.route(url).handler(methodHandler);
                        break;
                    case HEAD:
                        router.head(url).handler(methodHandler);
                        break;
                    case OPTIONS:
                        router.options(url).handler(methodHandler);
                        break;
                    case PUT:
                        router.put(url).handler(methodHandler);
                        break;
                    case POST:
                        router.post(url).handler(methodHandler);
                        break;
                    case DELETE:
                        router.delete(url).handler(methodHandler);
                        break;
                    case TRACE:
                        router.trace(url).handler(methodHandler);
                        break;
                    case CONNECT:
                        router.connect(url).handler(methodHandler);
                        break;
                    case PATCH:
                        router.patch(url).handler(methodHandler);
                        break;
                    default:
                        router.get(url).handler(methodHandler);
                        break;
                }
            }
        }
    }

}
