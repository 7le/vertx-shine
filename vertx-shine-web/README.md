# vertx-shine-web

### 🌈 startup

> 默认不开启集群， (若开启集群的话，默认为zookeeper,ignite可以选择) ，RouterHandlerFactory方法中第一个路径是扫描router（Verticle），
后一个是路由前缀。 下面有两种部署方式，可以任选。

```
public class ServerMain {

    public static void main(String[] args) {
        startByZookeeper();
    }

    private static void startByZookeeper() {
        //集成spring 不需要可以注释掉
        SpringUtils.init("spring.xml");
        //开启集群 如果不需要集群 就注释掉这句代码
        VerticleLauncher.isCluster = true;
        //开启guava eventbus
        VerticleLauncher.guavaEventBus = true;
        EventBusService.maxPoolSize = 100;
        VerticleLauncher.getStandardVertx(Vertx.vertx(), v -> {
            try {
                DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web", "shine")
                        .createRouter(), "top.arkstack.shine.handler", 7777, s -> {
                });
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        });

    private static void startByIgnite() {
        //集成spring 不需要可以注释掉
        SpringUtils.init("spring.xml");
        //开启集群 如果不需要集群 就注释掉这句代码
        VerticleLauncher.isCluster = true;
        VerticleLauncher.guavaEventBus = true;
        VerticleLauncher.cluster_mode = ClusterMode.IGNITE;
        VerticleLauncher.getStandardVertx(Vertx.vertx(), v -> {
            try {
                DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web", "shine")
                        .createRouter(), "top.arkstack.shine.handler", 7777, s -> {
                });
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        });

    private static void start() {
        //指定部署Verticle  true -> Worker Verticle
        VerticleLauncher.setVertxWithDeploy(Vertx.vertx(), v -> {
            try {
                DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web")
                        .createRouter(), 7000, s -> {
                });
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        }, HttpVerticle.class.getName(), tru
    }
}
```

### 💌 Verticle

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
        }, false, h -> routingContext.response().setStatusCode(200).end("It is amazing !"));
    }
}
```
⚠ 这里需要注意：使用``executeBlocking``会调用``worker``线程，``ordered`` 默认为``true``（相当于串行），当设置为``false``就是并行。
建议设置为``false``，因为当为``true``的时候，``worker``线程池中不同地方的调用，可能会出现阻塞，而且还不易排查。

### 🔥 集群配置

> 默认为zookeeper 默认的配置为default-zookeeper.json 需要自定义的配置的话在资源文件的根目录下覆盖就行，
使用ignite的话同理。

默认参数，可以自行修改：

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

    /**
     * 设置 guava eventbus 默认关闭
     */
    public static volatile boolean guavaEventBus = false;

```

### 🍐 集成guava eventbus

配置：
```
    /**
     * 线程池维护线程的最小数量 缺省大小为 cpu个数的 2倍
     */
    public volatile static int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 线程池维护线程的最大数量 缺省最大线程数为 cpu个数的4倍
     */
    public volatile static int maxPoolSize =Runtime.getRuntime().availableProcessors() * 4;

    /**
     * 线程存活保持时间
     */
    public volatile static long keepAliveTime = 60L;
```

打开:
``VerticleLauncher.guavaEventBus=true;``

发送：
``EventBusService.getEventBus().post("amazing"); ``


接收：
```
@EventSubscriber
public class TestGuavaEventBus {

    @AllowConcurrentEvents
    @Subscribe
    public void onMsg(String msg) {
        System.out.println("guava eventbus : It is " + msg + " !");
    }
}
```

### 🔒提供细粒度锁

使用demo：
```
    lock.lock(key);
    try {
        try {
            //需要加锁的代码
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } finally {
        lock.unlock(key);
    }
```


继续补充中 ⏳...