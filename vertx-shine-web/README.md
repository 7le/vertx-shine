# vertx-shine-web

> 启动 demo ，默认不开启集群， (集群使用ignite实现) ，RouterHandlerFactory方法中第一个路径是扫描router（Verticle），
后一个是路由前缀

```
public class ServerMain {

    public static void main(String[] args) {
        //集成spring
        SpringUtils.init("spring.xml");
        //开启集群 如果不需要集群 就注释掉这句代码
        VerticleLauncher.isCluster = true;
        VerticleLauncher.getStandardVertx(Vertx.vertx(), v -> {
            try {
                DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web", "shine")
                        .createRouter(), 7777);
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        });
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

> 集群 是使用了ignite 默认的配置位default-ignite.xml 需要自定义的配置的话增加ignite.xml在资源文件的根目录下

> 默认参数，可以自行修改

```
    /**
     * 是否集群模式
     */
    public static volatile boolean isCluster = false;

    /**
     * 默认WorkerPool 大小为50
     */
    public static volatile int workerPoolSize = 100;

    /**
     * 默认重新连接次数
     */
    public static volatile int eventBusReconnectAttempts = 50;

    /**
     * 设置集群 ping 间隔值（ms）
     */
    public static volatile int clusterPingInterval = 5000;

```

继续补充中...