package org.shine.web;

import io.vertx.core.Vertx;
import org.shine.web.verticle.RouterHandlerFactory;
import org.shine.web.verticle.VerticleLauncher;
import org.shine.web.vertx.DeployVertxServer;

import java.io.IOException;

/**
 * @author 7le
 * @Description: test 入口
 * @date 2017年12月6日
 * @since v1.0.0
 */
public class ServerMain {

    public static void main(String[] args) throws IOException {
        VerticleLauncher.isCluster = true;
        VerticleLauncher.getStandardVertx(Vertx.vertx());
        DeployVertxServer.startDeploy(new RouterHandlerFactory("org.shine.web","shine")
                .createRouter(),"org.shine.web",7777);

    }
}
