package top.arkstack.shine.web;

import io.vertx.core.Vertx;
import top.arkstack.shine.web.verticle.RouterHandlerFactory;
import top.arkstack.shine.web.verticle.VerticleLauncher;
import top.arkstack.shine.web.vertx.DeployVertxServer;

import java.io.IOException;

/**
 * @author 7le
 * @Description: test 入口
 * @date 2017年12月6日
 * @since v1.0.0
 */
public class ServerMain {

    public static void main(String[] args) throws IOException {
        //开启集群
        VerticleLauncher.isCluster = true;
        VerticleLauncher.getStandardVertx(Vertx.vertx());
        DeployVertxServer.startDeploy(new RouterHandlerFactory("top.arkstack.shine.web","shine")
                .createRouter(),"top.arkstack.shine.web",7777);

    }
}
