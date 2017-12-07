package org.shine.web.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.shine.web.vertx.VertxRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author 7le
 * @Description: router 扫描注册器
 * @date 2017年12月7日
 * @since v1.0.0
 */
public class RouterRegistryHandlersFactory extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(RouterHandlerFactory.class);

    protected Router router;

    protected HttpServer server;

    /**
     * 默认http server端口
     */
    public static volatile int serverPort = 8080;


    public RouterRegistryHandlersFactory(int port) {
        this.router = VertxRouter.getRouter();
        if (port > 0) {
            serverPort = port;
        }
    }

    public RouterRegistryHandlersFactory(Router router) {
        Objects.requireNonNull(router, "The router is empty.");
        this.router = router;
    }

    public RouterRegistryHandlersFactory(Router router, int port) {
        this.router = router;
        if (port > 0) {
            serverPort = port;
        }
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        HttpServerOptions options = new HttpServerOptions().setMaxWebsocketFrameSize(1000000).setPort(serverPort);
        server = vertx.createHttpServer(options).requestHandler(router::accept)
                .listen(result -> {
                    if (result.succeeded()) {
                        future.complete();
                        System.out.println("Port : " + serverPort + " is already started");
                    } else {
                        future.fail(result.cause());
                    }
                });
    }
}
