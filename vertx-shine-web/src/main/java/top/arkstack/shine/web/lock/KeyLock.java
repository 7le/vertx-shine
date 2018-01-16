package top.arkstack.shine.web.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

/**
 * 细粒度的锁 针对key来加锁
 *
 * @author : 7le
 * @since v1.0.5
 */
public class KeyLock<K> {

    /**
     * 保存所有锁定的key及其信号量
     */
    private final ConcurrentMap<K, Semaphore> map = new ConcurrentHashMap<>();

    /**
     * 保存每个线程锁定的key及其锁定计数
     */
    private final ThreadLocal<Map<K, LockInfo>> threadLocal = new ThreadLocal<Map<K, LockInfo>>() {
        @Override
        protected Map<K, LockInfo> initialValue() {
            return new HashMap<>(16);
        }
    };

    /**
     * 释放key，唤醒其他等待此key的线程
     *
     */
    public void unlock(K key) {
        if (key == null) {
            return;
        }
        LockInfo lock = threadLocal.get().get(key);
        if (lock != null && --lock.count == 0) {
            lock.current.release();
            map.remove(key, lock.current);
            threadLocal.get().remove(key);
        }
    }

    /**
     * key加锁，其他操作此key的线程将进入等待，直到锁被释放
     * 底层是通过hashcode和equals来判断key是否相同，因此key必须实现{@link java.lang.Object#hashCode()}和
     * {@link java.lang.Object#equals(Object)}方法
     *
     * */
    public void lock(K key) {
        if (key == null) {
            return;
        }
        LockInfo lock = threadLocal.get().get(key);
        if (lock == null) {
            Semaphore current = new Semaphore(1);
            current.acquireUninterruptibly();
            Semaphore previous = map.put(key, current);
            if (previous != null) {
                previous.acquireUninterruptibly();
            }
            threadLocal.get().put(key, new LockInfo(current));
        } else {
            lock.count++;
        }
    }

    /**
     * key加锁，其他操作此key的线程将进入等待，直到锁被释放
     * 可中断
     *
     */
    public void lockInterrupt(K key) throws InterruptedException {
        if (key == null) {
            return;
        }
        LockInfo lock = threadLocal.get().get(key);
        if (lock == null) {
            Semaphore current = new Semaphore(1);
            current.acquire();
            Semaphore previous = map.put(key, current);
            if (previous != null) {
                previous.acquire();
            }
            threadLocal.get().put(key, new LockInfo(current));
        } else {
            lock.count++;
        }
    }

    /**
     * 锁定多个key
     *
     */
    public void lock(K[] keys) {
        if (keys == null) {
            return;
        }
        for (K key : keys) {
            lock(key);
        }
    }

    /**
     * 锁定多个key，可中断
     *
     */
    public void lockInterrupt(K[] keys) throws InterruptedException {
        if (keys == null) {
            return;
        }
        for (K key : keys) {
            lockInterrupt(key);
        }
    }

    /**
     * 释放多个key
     *
     */
    public void unlock(K[] keys) {
        if (keys == null) {
            return;
        }
        for (K key : keys) {
            unlock(key);
        }
    }

    private static class LockInfo {
        private final Semaphore current;
        private int count;

        private LockInfo(Semaphore current) {
            this.current = current;
            this.count = 1;
        }
    }
}
