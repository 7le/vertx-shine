package top.arkstack.shine.web;

import com.alibaba.fastjson.JSON;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import top.arkstack.shine.web.annotations.RequestMethod;
import top.arkstack.shine.web.annotations.RouteHandler;
import top.arkstack.shine.web.annotations.RouteMapping;
import top.arkstack.shine.web.bean.MonitorInfo;
import top.arkstack.shine.web.verticle.VerticleLauncher;

/**
 * test verticle
 *
 * @author 7le
 * @since v1.0.0
 */
@RouteHandler
public class VideoVerticle {

    private Vertx vertx = VerticleLauncher.getStandardVertx();

    @RouteMapping(method = RequestMethod.GET, value = "/test")
    public Handler<RoutingContext> test() {
        return routingContext -> vertx.executeBlocking(future -> {
            EventBus eventBus=vertx.eventBus();
            eventBus.send("address","gogo");
            System.out.println("executeBlocking: "+Thread.currentThread().getName());
            System.out.println("type : " + routingContext.request().getParam("type"));
            //需要调用complete  FutureImpl -> setHandler 需要
            future.complete(1);
        }, h -> routingContext.response().setStatusCode(200).end("It is amazing !"));
    }

    /**
     * restful
     */
    @RouteMapping(method = {RequestMethod.GET,RequestMethod.HEAD}, value = "/server/:type.htm")
    public Handler<RoutingContext> getStatus() {
        return routingContext -> {
            if ("monitor".equals(routingContext.request().getParam("type"))) {
                MonitorInfo info = new MonitorInfo();
                // 剩余内存
                info.setFreeMemory(Runtime.getRuntime().freeMemory());
                // 可使用内存
                info.setTotalMemory(Runtime.getRuntime().totalMemory());
                // 最大可使用内存
                info.setMaxMemory(Runtime.getRuntime().maxMemory());
                // 线程总数
                ThreadGroup tg;
                for (tg = Thread.currentThread().getThreadGroup(); tg.getParent() != null; tg = tg.getParent()) ;
                info.setTotalThread(tg.activeCount());
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(JSON.toJSONString(info));
            } else {
                routingContext.response()
                        .putHeader("content-type", "text/plain")
                        .end("Hello!");
            }

        };
    }
}
