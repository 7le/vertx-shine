# vertx-shine-web

> 启动 demo ，默认不开启集群， (集群使用ignite实现)

```
public class ServerMain {

    public static void main(String[] args) throws IOException {
        //开启集群
        VerticleLauncher.isCluster = true;
        VerticleLauncher.getStandardVertx(Vertx.vertx());
        DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web","shine")
                .createRouter(),"top.arkstack.shine.web",7777);

    }
}
```

> Verticle demo 跟springmvc controller 差不多

```
@RouteHandler
public class VideoVerticle {

    private Vertx vertx = VerticleLauncher.getStandardVertx();

    @RouteMapping(method = RequestMethod.GET, value = "test")
    public Handler<RoutingContext> test() {
        return routingContext -> vertx.executeBlocking(future -> {
            System.out.println("executeBlocking: "+Thread.currentThread().getName());
            System.out.println("type : " + routingContext.request().getParam("type"));
            //需要调用complete  FutureImpl -> setHandler 需要
            future.complete(1);
        }, h -> routingContext.response().setStatusCode(200).end("It is amazing !"));
    }
}
```

继续补充中...