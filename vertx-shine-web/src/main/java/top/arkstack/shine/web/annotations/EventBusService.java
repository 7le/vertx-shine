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

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(8, 50,
            60L, TimeUnit.SECONDS,
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
