package top.arkstack.shine.web.vertx;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import top.arkstack.shine.web.verticle.RegistryHandlersFactory;
import top.arkstack.shine.web.verticle.RouterRegistryHandlersFactory;
import top.arkstack.shine.web.verticle.VerticleLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 注册vertx相关服务
 *
 * @author 7le
 * @since v1.0.0
 */
public class DeployVertxServer {

    private static Logger log = LoggerFactory.getLogger(DeployVertxServer.class);

    public static void startDeploy(int port, Handler<Vertx> handler) throws IOException {
        log.trace("Start Deploy....");
        VerticleLauncher.getStandardVertx().deployVerticle(new RouterRegistryHandlersFactory(port));
        handler.handle( VerticleLauncher.getStandardVertx());
    }

    public static void startDeploy(Router router, int port, Handler<Vertx> handler) throws IOException {
        log.trace("Start Deploy....");
        VerticleLauncher.getStandardVertx().deployVerticle(new RouterRegistryHandlersFactory(router, port));
        handler.handle( VerticleLauncher.getStandardVertx());
    }

    public static void startDeploy(Router router, String handlerScan, int port, Handler<Vertx> handler) throws IOException {
        log.trace("Start Deploy....");
        VerticleLauncher.getStandardVertx().deployVerticle(new RouterRegistryHandlersFactory(router, port));
        log.trace("Start registry handler....");
        new RegistryHandlersFactory(handlerScan).registerVerticle();
        handler.handle( VerticleLauncher.getStandardVertx());
    }

    public static void startDeploy(Router router, String handlerScan, String appPrefix,
                                   int port, Handler<Vertx> handler) throws IOException {
        log.trace("Start Deploy....");
        VerticleLauncher.getStandardVertx().deployVerticle(new RouterRegistryHandlersFactory(router, port));
        log.trace("Start registry handler....");
        new RegistryHandlersFactory(handlerScan, appPrefix).registerVerticle();
        handler.handle( VerticleLauncher.getStandardVertx());
    }

    public static void startDeploy(String handlerScan, String appPrefix, Handler<Vertx> handler) throws IOException {
        log.trace("Start registry handler....");
        new RegistryHandlersFactory(handlerScan, appPrefix).registerVerticle();
        handler.handle( VerticleLauncher.getStandardVertx());
    }
}
