# vertx-shine-web

> 启动 demo ，默认不开启集群， (若开启集群的话，默认为zookeeper,ignite可以选择) ，RouterHandlerFactory方法中第一个路径是扫描router（Verticle），
后一个是路由前缀。 下面有两种部署方式，可以任选。

```
public class ServerMain {

    public static void main(String[] args) {
        startByZookeeper();
    }

    private static void startByZookeeper(){
        //集成spring 不需要可以注释掉
        SpringUtils.init("spring.xml");
        //开启集群 如果不需要集群 就注释掉这句代码
        VerticleLauncher.isCluster = true;
        VerticleLauncher.getStandardVertx(Vertx.vertx(), v -> {
            try {
                DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web", "shine")
                        .createRouter(), "top.arkstack.shine.handler", 7777);
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        });
    }

    private static void startByIgnite(){
        //集成spring 不需要可以注释掉
        SpringUtils.init("spring.xml");
        //开启集群 如果不需要集群 就注释掉这句代码
        VerticleLauncher.isCluster = true;
        VerticleLauncher.cluster_mode=ClusterMode.IGNITE;
        VerticleLauncher.getStandardVertx(Vertx.vertx(), v -> {
            try {
                DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web", "shine")
                        .createRouter(), "top.arkstack.shine.handler", 7777);
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        });
    }

    private static void start()
    {
        //指定部署Verticle  true -> Worker Verticle
        try {
            VerticleLauncher.setVertxWithDeploy(Vertx.vertx(), v -> {
                try {
                    DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web")
                            .createRouter(), 7000);
                } catch (IOException e) {
                    System.out.println("启动失败: " + e.getMessage());
                }
            }, HttpVerticle.class.getName(), true);
        } catch (InterruptedException e) {
            System.out.println("启动失败: " + e.getMessage());
        }
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

> 集群 默认为zookeeper 默认的配置为default-zookeeper.json 需要自定义的配置的话在资源文件的根目录下覆盖就行，
使用ignite的话同理。

> 默认参数，可以自行修改

```
    /**
     * 是否集群模式
     */
    public static volatile boolean isCluster = false;

    /**
     * 设置集群方式 默认为zookeeper
     */
    public static volatile ClusterMode cluster_mode = ClusterMode.ZOOKEEPER;

    /**
     * 默认WorkerPool 大小为100
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

    /**
     * 设置eventbus 通信端口，防止多vertx实例时方式冲突，不设置自动分配
     */
    public static volatile int eventbusPort = -1;

```

继续补充中...