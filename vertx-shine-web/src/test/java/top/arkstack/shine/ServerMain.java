package top.arkstack.shine;

import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.StaticHandler;
import top.arkstack.shine.web.HttpVerticle;
import top.arkstack.shine.web.annotations.EventBusService;
import top.arkstack.shine.web.bean.ClusterMode;
import top.arkstack.shine.web.util.SpringUtils;
import top.arkstack.shine.web.verticle.RouterHandlerFactory;
import top.arkstack.shine.web.verticle.VerticleLauncher;
import top.arkstack.shine.web.vertx.DeployVertxServer;

import java.io.IOException;

/**
 * test 入口
 *
 * @author 7le
 * @since v1.0.0
 */
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
                    //发布静态资源 路由可以自行修改
                    RouterHandlerFactory.getRouter().route("/static/*").handler(StaticHandler.create("static"));
                });
            } catch (IOException e) {
                System.out.println("启动失败: " + e.getMessage());
            }
        });
    }

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
    }

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
        }, HttpVerticle.class.getName(), true);
    }
}
