package top.arkstack.shine.web.verticle;

import com.google.common.base.Strings;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.internal.util.typedef.F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.arkstack.shine.web.util.IpUtils;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Verticle Launcher
 *
 * @author 7le
 * @since v1.0.0
 */
public class VerticleLauncher {

    private static Logger log = LoggerFactory.getLogger(VerticleLauncher.class);

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

    /**
     * vertx 对象
     */
    private static Vertx standardVertx;

    private static Vertx localVertx;

    /**
     * 暂用ignite
     */
    private static final String CONFIG_FILE = "ignite.xml";

    private static final String DEFAULT_CONFIG_FILE = "default-ignite.xml";

    private static void init(Vertx vertx) {
        Objects.requireNonNull(vertx, "The vertx is empty.");
        standardVertx = vertx;
        localVertx = vertx;
    }

    private static void init(Vertx vertx, Handler<Vertx> handler) {
        Objects.requireNonNull(vertx, "The vertx is empty.");
        standardVertx = vertx;
        localVertx = vertx;
        handler.handle(standardVertx);
    }

    private static void setClusterVertxWithDeploy(Handler<Vertx> handler, VertxOptions options, Consumer<Vertx> runner) {
        options.setClusterManager(new IgniteClusterManager(loadConfiguration()));
        Vertx.clusteredVertx(options, vertxAsyncResult -> {
            if (vertxAsyncResult.succeeded()) {
                init(vertxAsyncResult.result());
                runner.accept(vertxAsyncResult.result());
                handler.handle(vertxAsyncResult.result());
            } else {
                System.out.println("Can't create cluster");
                System.exit(1);
            }
        });
    }

    /**
     * 部署：可指定部署的verticle (Worker Verticle)
     *
     * @param vertx    standardVertx
     * @param verticle 需要启动的指定verticle
     * @param worker   Worker Verticle
     * @throws InterruptedException
     */
    public static void setVertxWithDeploy(Vertx vertx, Handler<Vertx> handler, String verticle, boolean worker) throws InterruptedException {
        String ip = IpUtils.getIpAddress();
        VertxOptions options = new VertxOptions().setClustered(true).setClusterHost(ip)
                .setWorkerPoolSize(workerPoolSize).setClusterHost(ip);
        EventBusOptions eventBusOptions = options.getEventBusOptions();
        if (eventBusOptions == null) {
            eventBusOptions = new EventBusOptions();
        }
        options.setEventBusOptions(eventBusOptions.setReconnectAttempts(eventBusReconnectAttempts)
                .setClusterPingInterval(clusterPingInterval).setHost(ip));
        DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(worker);
        VerticleLauncher.setVertxWithDeploy(vertx, handler, verticle, options, deploymentOptions, null);
    }

    private static void setVertxWithDeploy(Vertx vertx, Handler<Vertx> handler, String verticle, VertxOptions options,
                                                  DeploymentOptions deploymentOptions, Handler<AsyncResult<String>> completionHandler) {
        if (options == null) {
            options = new VertxOptions();
        }
        Consumer<Vertx> runner = v -> {
            Handler<AsyncResult<String>> h = completionHandler;
            if (h == null) {
                h = a -> {
                };
            }
            try {
                if (deploymentOptions != null) {
                    if (!Strings.isNullOrEmpty(verticle))
                        v.deployVerticle(verticle, deploymentOptions, h);
                } else {
                    if (!Strings.isNullOrEmpty(verticle))
                        v.deployVerticle(verticle, h);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            setClusterVertxWithDeploy(handler, options, runner);
        } else {
            init(vertx);
            runner.accept(vertx);
        }
    }

    /**
     * 读取配置
     */
    private static IgniteConfiguration loadConfiguration() {
        ClassLoader ctxClsLoader = Thread.currentThread().getContextClassLoader();

        InputStream is = null;

        if (ctxClsLoader != null) {
            is = ctxClsLoader.getResourceAsStream(CONFIG_FILE);
        }
        if (is == null) {
            Class<VerticleLauncher> cls = VerticleLauncher.class;
            is = cls.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (is == null) {
                is = cls.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
                System.out.println("Using default configuration.");
            }
        }
        try {
            return F.first(IgnitionEx.loadConfigurations(is).get1());
        } catch (IgniteCheckedException e) {
            System.out.println("Configuration loading error:" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置集群
     */
    private static Vertx setClusterVertx(Vertx vertx, Handler<Vertx> handler) {
        Objects.requireNonNull(vertx, "The vertx of cluster is empty.");
        try {
            setVertxWithDeploy(vertx, handler, null, false);
        } catch (Exception e) {
            log.error("启动集群失败", e);
        }
        return standardVertx;
    }

    /**
     * 获得标准vertx对象
     */
    public static Vertx getStandardVertx(Vertx vertx, Handler<Vertx> handler) {
        if (!isCluster) {
            init(vertx, handler);
        } else {
            try {
                setClusterVertx(vertx, handler);
            } catch (Exception e) {
                log.error("启动失败", e);
            }
        }
        return standardVertx;
    }

    /**
     * 获得标准vertx对象
     */
    public static Vertx getStandardVertx() {
        Objects.requireNonNull(standardVertx, "Please perform vertx initialization, otherwise will block the subsequent operations.");
        return standardVertx;
    }

    /**
     * 获得本地vertx 初始化时本地保存的vertx对象
     */
    public static Vertx getLocalVertx() {
        Objects.requireNonNull(localVertx, "Please perform vertx initialization, otherwise will block the subsequent operations.");
        return localVertx;
    }
}
