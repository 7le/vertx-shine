package top.arkstack.shine.web.vertx;

import io.vertx.ext.web.Router;
import top.arkstack.shine.web.verticle.VerticleLauncher;

/**
 * 获得全局的Router对象
 *
 * @author 7le
 * @since v1.0.0
 */
public class VertxRouter {

    private static Router router;
    private static VertxRouter instance;

    private VertxRouter() {
        router = Router.router(VerticleLauncher.getStandardVertx());
    }

    public static VertxRouter getInstance() {
        if (instance == null) {
            instance = new VertxRouter();
        }
        return instance;
    }

    /**
     * 获得当前Router
     */
    public static Router getRouter() {
        if (instance == null) {
            instance = new VertxRouter();
        }
        return router;
    }
}
