package top.arkstack.shine.web;

import io.vertx.core.AbstractVerticle;

/**
 * 指定部署 Verticle
 *
 * @author 7le
 * @since v1.0.4
 */
public class HttpVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        getVertx().setPeriodic(60000, job -> System.out.println("===================================="+Thread.currentThread().getName()));
    }
}
