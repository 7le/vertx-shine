package lock;

import top.arkstack.shine.web.lock.KeyLock;

import java.util.Date;
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
public class LockTest {


    public static void main(String[] args) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 50,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        int i = 0;
        Scanner scan = new Scanner(System.in);
        String a = "123";
        //System.out.println(a.hashCode());
        Test test = new Test();
        while (i < 10) {
            a = scan.next();
            //System.out.println(a.hashCode());
            test.setKey(a);
            System.out.println("=====================start================= " + a + new Date());
            executor.execute(test);
            i++;
        }

    }

    static class Test implements Runnable {

        private final KeyLock<String> lock = new KeyLock<>();

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            String a = key;
            lock.lock(a);
            try {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("=====================end================= " + a + new Date());
            } finally {
                lock.unlock(a);
            }

            /*synchronized (key){
                try {
                    Thread.sleep(10000);
                    System.out.println(key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }
}
