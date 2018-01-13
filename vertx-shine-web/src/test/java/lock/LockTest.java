package lock;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * ==============================
 * count = 1000000/tThread Count = 10
 * Stamped  = 271
 * Reentrant  = 268
 * synchronized  = 392
 * Semaphore  = 283
 * ==============================
 * ==============================
 * count = 2000000/tThread Count = 20
 * Stamped  = 521
 * Reentrant  = 458
 * synchronized  = 747
 * Semaphore  = 547
 * ==============================
 * ==============================
 * count = 3000000/tThread Count = 30
 * Stamped  = 725
 * Reentrant  = 669
 * synchronized  = 1150
 * Semaphore  = 825
 * ==============================
 * ==============================
 * count = 4000000/tThread Count = 40
 * Stamped  = 949
 * Reentrant  = 905
 * synchronized  = 1534
 * Semaphore  = 1081
 * ==============================
 *
 * 单同步写的情况 性能循序为 ReentrantLock StampedLock Semaphore synchronized
 *
 * StampedLock（乐观锁）的应用场景更适于读远大于写
 *
 * @author : 7le
 * @since v1.0.5
 */
public class LockTest {
    private static long COUNT = 1000000;
    private static StampedLock stampedLock = new StampedLock();
    private static Lock reentrantLock = new ReentrantLock();
    private static Object syncLock = new Object();
    private static Semaphore mutex = new Semaphore(1);

    private static long reentrantCounter = 0;
    private static long stampedCounter = 0;
    private static long syncCounter = 0;
    private static long semaCounter = 0;

    public static void main(String[] args) {
        for (int i = 1; i < 5; i++) {
            ExecutorService executor = Executors.newFixedThreadPool(10 * i);
            test("", COUNT * i, 10 * i, executor);
        }
    }

    static long getStampedLock() {
        long stamp = stampedLock.writeLock();
        try {
            return stampedCounter;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    static long getReentrantLock() {
        reentrantLock.lock();
        try {
            return reentrantCounter;
        } finally {
            reentrantLock.unlock();
        }
    }

    static long getSync() {
        synchronized (syncLock) {
            return syncCounter;
        }
    }

    static long getSemaphore() throws InterruptedException {
        mutex.acquire();

        try {
            return semaCounter;
        } finally {
            mutex.release();
        }
    }

    static long getLockInc() {
        reentrantLock.lock();
        try {
            return ++reentrantCounter;
        } finally {
            reentrantLock.unlock();
        }
    }

    static long getSyncInc() {
        synchronized (syncLock) {
            return ++syncCounter;
        }
    }

    static class SemaTest extends Test {

        public SemaTest(String id, CyclicBarrier barrier, long count,
                        int threadNum, ExecutorService executor) {
            super(id, barrier, count, threadNum, executor);
        }

        @Override
        protected void test() {
            try {
                getSemaphore();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    static class StampedLockTest extends Test {

        public StampedLockTest(String id, CyclicBarrier barrier, long count,
                               int threadNum, ExecutorService executor) {
            super(id, barrier, count, threadNum, executor);
        }

        @Override
        protected void test() {
            getStampedLock();
        }

    }

    static class ReentrantLockTest extends Test {

        public ReentrantLockTest(String id, CyclicBarrier barrier, long count,
                                 int threadNum, ExecutorService executor) {
            super(id, barrier, count, threadNum, executor);
        }

        @Override
        protected void test() {
            getReentrantLock();
        }

    }

    static class SyncTest extends Test {

        public SyncTest(String id, CyclicBarrier barrier, long count,
                        int threadNum, ExecutorService executor) {
            super(id, barrier, count, threadNum, executor);
        }

        @Override
        protected void test() {
            getSync();
        }

    }

    public static void test(String id, long count, int threadNum, ExecutorService executor) {

        final CyclicBarrier barrier = new CyclicBarrier(threadNum + 1, () -> {
        });

        System.out.println("==============================");
        System.out.println("count = " + count + "/t" + "Thread Count = " + threadNum);
        new StampedLockTest("Stamped ", barrier, COUNT, threadNum, executor).startTest();
        new ReentrantLockTest("Reentrant ", barrier, COUNT, threadNum, executor).startTest();
        new SyncTest("synchronized ", barrier, COUNT, threadNum, executor).startTest();
        new SemaTest("Semaphore ", barrier, COUNT, threadNum, executor).startTest();
        System.out.println("==============================");
    }

    static abstract class Test {

        private String id;
        private CyclicBarrier barrier;
        private long count;
        private int threadNum;
        private ExecutorService executor;

        Test(String id, CyclicBarrier barrier, long count, int threadNum,
             ExecutorService executor) {
            this.id = id;
            this.barrier = barrier;
            this.count = count;
            this.threadNum = threadNum;
            this.executor = executor;
        }

        void startTest() {
            long start = System.currentTimeMillis();
            for (int j = 0; j < threadNum; j++) {
                executor.execute(() -> {
                    for (int i = 0; i < count; i++) {
                        test();
                    }
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 所有线程执行完成之后，才会跑到这一步
            long duration = System.currentTimeMillis() - start;
            System.out.println(id + " = " + duration);
        }

        protected abstract void test();
    }
}
