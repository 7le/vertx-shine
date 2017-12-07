package org.shine.web;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.shine.web.annotations.RequestMethod;
import org.shine.web.annotations.RouteHandler;
import org.shine.web.annotations.RouteMapping;
import org.shine.web.verticle.VerticleLauncher;

/**
 * @author 7le
 * @Description: test verticle
 * @date 2017年12月7日
 * @since v1.0.0
 */
@RouteHandler
public class VideoVerticle {

    private Vertx vertx = VerticleLauncher.getStandardVertx();

    @RouteMapping(method = RequestMethod.GET, value = "test")
    public Handler<RoutingContext> test() {
        return handler -> {
            vertx.executeBlocking(s -> {
                System.out.println("yoyo !");
            }, h -> {});
            handler.response().setStatusCode(200).end("It is amazing !");
        };
    }
}
