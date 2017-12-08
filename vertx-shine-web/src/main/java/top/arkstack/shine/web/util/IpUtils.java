package top.arkstack.shine.web.util;

import com.google.common.base.Strings;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author : 7le
 * @description: IpUtils
 * @date: 2017/11/16
 */
public class IpUtils {

    private final static Logger LOG = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 本机获取ip地址
     *
     * @return
     */
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
        for (String s : ipArray) {// 过滤掉局域网ip
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
