package top.arkstack.shine.handler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import top.arkstack.shine.web.annotations.Verticle;
import top.arkstack.shine.web.verticle.VerticleLauncher;

/**
 * eventbus test
 *
 * @author 7le
 * @since v1.0.5
 */
@Verticle
public class TestEventBusHandler extends AbstractVerticle {

    private Vertx vertx = VerticleLauncher.getStandardVertx();

    @Override
    public void start() throws Exception {
        System.out.println(this.getClass().getSimpleName() + " deploy 成功");
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("address", s -> System.out.println(s.body()));
    }
}
