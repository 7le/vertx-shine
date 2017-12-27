package top.arkstack.shine.handler;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import top.arkstack.shine.web.annotations.EventSubscriber;

/**
 * guava eventbus test
 *
 * @author 7le
 * @since v1.0.5
 */
@EventSubscriber
public class TestGuavaEventBus {

    @AllowConcurrentEvents
    @Subscribe
    public void onMsg(String msg) {
        System.out.println("guava eventbus : It is " + msg + " !");
    }
}
