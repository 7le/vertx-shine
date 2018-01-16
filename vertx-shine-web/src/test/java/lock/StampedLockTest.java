package lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

/**
 * StampedLock Test
 *
 * @author : 7le
 * @since v1.0.5
 */
public class StampedLockTest {

    private static final Integer LIMIT = 50;
    private List<Integer> buffer = new ArrayList<>();

    private StampedLock lock = new StampedLock();
    private Integer count = 0;
    private Integer total = 0;

    /**
     * 有并发问题  readLock 可重读
     * @param data
     */
    private void put(Integer data) {
        List<Integer> tmp = new ArrayList<>();
        long writeLock = lock.writeLock();
        try {
            buffer.add(data);
            total++;
        } finally {
            lock.unlockWrite(writeLock);
        }

        long readLock = lock.readLock();
        try {
            if (buffer.size() > LIMIT) {
                tmp = new ArrayList<>(buffer);
                System.out.println(Thread.currentThread().getName() + "\ttmp.size():" + tmp.size() + "\tbuffer.size():" + buffer.size());
                buffer.clear();
            }
        } finally {
            lock.unlockRead(readLock);
        }
        count += tmp.size();
    }

    /**
     * 乐观读 适合于 读 远大于 写的场景
     * @param data
     */
    private void put2(Integer data) {
        List<Integer> tmp;
        long writeLock = lock.writeLock();
        try {
            buffer.add(data);
            total++;
        } finally {
            lock.unlockWrite(writeLock);
        }
        long readLock = lock.readLock();
        try {
            if (buffer.size() >= LIMIT) {
                long newWriteLock = lock.tryConvertToWriteLock(readLock);
                if (newWriteLock != 0) {
                    tmp = new ArrayList<>(buffer);
                    System.out.println(Thread.currentThread().getName() + "\ttmp.size():" + tmp.size() + "\tbuffer.size():" + buffer.size());
                    readLock = newWriteLock;
                    count += tmp.size();
                    buffer.clear();
                } else {
                    System.out.println("fail");
                }
            }
        } finally {
            lock.unlock(readLock);
        }
    }

    /**
     * 写锁  独占 Exclusively acquires the lock
     * @param data
     */
    private void put3(Integer data) {
        List<Integer> tmp;
        long writeLock = lock.writeLock();
        try {
            buffer.add(data);
            total++;
            if (buffer.size() >= LIMIT) {
                tmp = new ArrayList<>(buffer);
                System.out.println(Thread.currentThread().getName() + "\ttmp.size():" + tmp.size() + "\tbuffer.size():" + buffer.size());
                count += tmp.size();
                buffer.clear();
            }
        } finally {
            lock.unlockWrite(writeLock);
        }
    }

    /**
     * synchronized
     * @param data
     */
    private synchronized void put4(Integer data) {
        List<Integer> tmp;
        buffer.add(data);
        total++;
        if (buffer.size() > LIMIT) {
            tmp = new ArrayList<>(buffer);
                System.out.println(Thread.currentThread().getName() + "\ttmp.size():" + tmp.size() + "\tbuffer.size():" + buffer.size());
            count += tmp.size();
            buffer.clear();
        }
    }



    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        StampedLockTest test = new StampedLockTest();
        int total = 10000_0000;
        int threadNum = 500;
        int per = total / threadNum;
        IntStream.range(0, threadNum).parallel().forEach(j ->
                IntStream.range(0, per).forEach(
                        test::put2)
        );

        System.out.println("count = " + test.count);
        System.out.println("buffer.size = " + test.buffer.size());
        System.out.println("total = " + test.total);
        long end = System.currentTimeMillis();
        System.out.println("spend:" + (end - start) + "ms");

    }

}
