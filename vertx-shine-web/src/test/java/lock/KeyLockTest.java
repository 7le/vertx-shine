package lock;

import top.arkstack.shine.web.lock.KeyLock;

import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试keyLock
 *
 * @author : 7le
 * @since v1.0.5
 */
public class KeyLockTest {


    public static void main(String[] args) {
        test();
    }

    private static void test() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 50,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        KeyLock<String> key = new KeyLock<>();
        for (int i = 0; i < 16; i++) {
            executor.execute(() -> {
                int num = new Random().nextInt(5);
                key.lock("key" + num);
                try {
                    System.out.println("==start====" + new Date() + " " + Thread.currentThread().getName() + "  key: " + num);
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println("==end======" + new Date() + " " + Thread.currentThread().getName() + "  key: " + num);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    key.unlock("key" + num);
                }
            });
        }
    }
}
