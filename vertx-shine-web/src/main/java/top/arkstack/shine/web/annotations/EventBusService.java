package top.arkstack.shine.web.annotations;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 扫描注册guava eventbus
 *
 * @author : 7le
 * @since v1.0.5
 */
public class EventBusService {

    private static Logger log = LoggerFactory.getLogger(EventBusService.class);

    /**
     * 需要扫描注册的路径
     */
    private static volatile Reflections reflections = new Reflections("");

    private static EventBus eventBus;

    /**
     * 线程池维护线程的最小数量 缺省大小为 cpu个数的 2倍
     */
    public volatile static int corePoolSize = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 线程池维护线程的最大数量 缺省最大线程数为 cpu个数的4倍
     */
    public volatile static int maxPoolSize =Runtime.getRuntime().availableProcessors() << 2;

    /**
     * 线程存活保持时间
     */
    public volatile static long keepAliveTime = 60L;

    /**
     * get eventbus of guava
     */
    public static EventBus getEventBus() {
        Objects.requireNonNull(eventBus, "The eventBus of guava is null");
        return eventBus;
    }

    public static void unRegister(Object eventListener) {
        eventBus.unregister(eventListener);
    }

    public static void postEvent(Object event) {
        eventBus.post(event);
    }

    private static void register(Object eventListener) {
        eventBus.register(eventListener);
    }

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
            keepAliveTime, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryImpl("pool-api-handler-thread-"));

    private static class ThreadFactoryImpl implements ThreadFactory {
        private String prex;
        private AtomicInteger index = new AtomicInteger(0);

        ThreadFactoryImpl(String prex) {
            this.prex = prex;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, prex + index.getAndIncrement());
        }
    }

    public static void init() {
        eventBus = new AsyncEventBus(EXECUTOR);
        Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(EventSubscriber.class);
        handlers.forEach(handler -> {
            try {
                register(handler.newInstance());
            } catch (Exception e) {
                log.error("Error register {} ", handler, e);
            }
        });
    }
}
