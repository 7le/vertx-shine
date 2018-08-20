package top.arkstack.shine.web.util;

import com.google.common.base.Strings;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * IpUtils
 *
 * @author : 7le
 * @since v1.0.0
 */
public class IpUtils {

    private final static Logger LOG = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取IP
     * @param isIntranet 是否获取内网的ip
     * @return
     */
    public static String getIp(boolean isIntranet) {
        List<String> ips = getLocalIps();
        for (String ip : ips) {
            if (isIntranet) {
                if (isIntranetIp(ip)) {
                    return ip;
                }
            } else {
                if (!isIntranetIp(ip)) {
                    return ip;
                }
            }
        }
        return "";
    }

    private static List<String> getLocalIps() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String ip = inetAddresses.nextElement().getHostAddress();
                    // 排除 回环IP/ipv6 地址
                    if (ip.contains(":")) {
                        continue;
                    }
                    if (!Strings.isNullOrEmpty(ip)) {
                        ips.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ips;
    }

    /**
     * 判断是否为内网IP
     * tcp/ip协议中, 专门保留了三个IP地址区域作为私有地址, 其地址范围如下:
     * 10.0.0.0/8: 10.0.0.0～10.255.255.255
     * 172.16.0.0/12: 172.16.0.0～172.31.255.255
     * 192.168.0.0/16: 192.168.0.0～192.168.255.255
     */
    private static boolean isIntranetIp(String ip) {
        try {
            if (ip.startsWith("10.") || ip.startsWith("192.168.")) {
                return true;
            }
            // 172.16.x.x～172.31.x.x
            String[] ns = ip.split("\\.");
            int ipSub = Integer.valueOf(ns[0] + ns[1]);
            if (ipSub >= 17216 && ipSub <= 17231) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取客户端的真实ip
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServerRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (LOG.isDebugEnabled()) {
            LOG.debug("x-forwarded-for = {}", ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Proxy-Client-IP = {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (LOG.isDebugEnabled()) {
                LOG.debug("WL-Proxy-Client-IP = {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.remoteAddress().toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("RemoteAddr-IP = {}", ip);
            }
        }
        if (!Strings.isNullOrEmpty(ip)) {
            ip = ip.split(":")[0];
        }
        String[] ipArray = ip.split(",");
        // 过滤掉局域网ip
        for (String s : ipArray) {
            s = s.trim();
            if (!(s.startsWith("10.") || s.startsWith("192."))) {
                return s;
            }
        }

        if (!Strings.isNullOrEmpty(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}
