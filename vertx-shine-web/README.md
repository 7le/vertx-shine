# vertx-shine-web

> 启动 demo ，默认不开启集群， (集群使用ignite实现)

```
public class ServerMain {

    //开启集群 如果不需要集群 就注释掉这句代码
            VerticleLauncher.isCluster = true;
            VerticleLauncher.getStandardVertx(Vertx.vertx(), v -> {
                try {
                    DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web", "shine")
                            .createRouter(), "top.arkstack.shine.web", 7777);
                } catch (IOException e) {
                    System.out.println("启动失败: " + e.getMessage());
                }
            });
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

> 集群 是使用了ignite 默认的配置位default-ignite.xml 需要自定义的配置的话增加ignite.xml在资源文件的根目录下

继续补充中...