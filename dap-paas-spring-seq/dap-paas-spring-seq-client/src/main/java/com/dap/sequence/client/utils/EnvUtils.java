package com.dap.sequence.client.utils;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class EnvUtils {

    /**
     * 获取当前环境IP地址
     *
     * @return
     */
    public static String getAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual()
                        || !netInterface.isUp()) {
                    continue;
                }

//                if (!netInterface.getDisplayName().contains("Intel")
//                        && !netInterface.getDisplayName().contains("Realtek")) {
//                    continue;
//                }
                if (!netInterface.getDisplayName().contains("VMware")
                        && !netInterface.getDisplayName().contains("Realtek")) {
                    continue;
                }
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

        }
        return "";

    }

}
