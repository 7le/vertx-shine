package top.arkstack.shine.web.bean;

import java.io.Serializable;
/**
 * 服务器状态监控详细信息
 *
 * @author 7le
 * @since v1.0.3
 */
public class MonitorInfo implements Serializable {

    /**
     * 可使用内存
     */
    private long totalMemory;

    /**
     * 剩余内存
     */
    private long freeMemory;

    /**
     * 最大可使用内存
     */
    private long maxMemory;

    /**
     * 线程总数
     */
    private int totalThread;

    private String sysVersion;

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public int getTotalThread() {
        return totalThread;
    }

    public void setTotalThread(int totalThread) {
        this.totalThread = totalThread;
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }
}