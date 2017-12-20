package top.arkstack.shine;

import io.vertx.core.Vertx;
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

        //指定部署Verticle  true -> Worker Verticle
        /*try {
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
        }*/
    }
}
