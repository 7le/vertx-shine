package top.arkstack.shine.web.cluster;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.spi.cluster.AsyncMultiMap;
import io.vertx.core.spi.cluster.ClusterManager;
import top.arkstack.shine.web.util.ReflectUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * zookeeper util
 *
 * @author : 7le
 * @since v1.0.5
 */
public class ZookeeperUtil {

    private static final String SUBS_MAP_MANE = "__vertx.subs";

    private static Map<String, HashSet> map = new HashMap<>();

    private static String DEFAULT = "default";

    /**
     * 默认使用 _ 为分隔符
     */
    public volatile static String SEPARATOR = "_";

    /**
     * 将注册的地址 刷入内存中
     */
    public static void flush(Vertx vertx, Handler<Object> handler) {
        ClusterManager mgr = ((VertxInternal) vertx).getClusterManager();
        mgr.getAsyncMultiMap(SUBS_MAP_MANE, r -> {
            if (r.succeeded()) {
                AsyncMultiMap<Object, Object> result = r.result();
                Object cache = ReflectUtils.getFieldValue(result, "cache");
                if (cache != null) {
                    ((Map<String, ?>) cache).forEach((k, v) -> {
                        Object ids = ReflectUtils.getFieldValue(v, "ids");
                        if (((ConcurrentHashSet) ids).size() > 0) {
                            String address = getAddressStr(k);
                            HashSet hashSet = map.get(getBusinessStr(address));
                            if (hashSet == null || hashSet.size() == 0) {
                                HashSet set = new HashSet();
                                set.add(address);
                                map.put(getBusinessStr(address), set);
                            } else {
                                hashSet.add(address);
                                map.put(getBusinessStr(address), hashSet);
                            }
                        }
                    });
                }
                if (handler != null) {
                    handler.handle(null);
                }
            }
        });
    }

    public static String getAddress(String business, Object key) {
        if (key == null) {
            key = DEFAULT;
        }
        HashSet addresses = map.get(business);
        if (addresses != null && addresses.size() > 0) {
            return addresses.toString().substring(1, addresses.toString().length() - 1)
                    .split(",")[(Math.abs(key.hashCode()) % addresses.size())].trim();
        } else {
            return null;
        }
    }

    private static String getAddressStr(String key) {
        return key.substring(key.lastIndexOf("/") + 1);
    }

    private static String getBusinessStr(String key) {
        return key.substring(0, key.lastIndexOf(SEPARATOR));
    }
}
