package com.dap.sequence.client.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSUtils {

    private static final Logger logger = LoggerFactory.getLogger(OSUtils.class);

    public static String hostIp = "";

    public static int getWorkId(){
        try {
            return getHostId(Inet4Address.getLocalHost().getHostAddress(),63);
        } catch (UnknownHostException e) {
           return new SecureRandom().nextInt(32);
        }
    }


    //项目启动只获取一次IP
    static {
        hostIp = getHostIp();
    }

    /**
     * 获取当前环境IP地址
     *
     * @return
     */
    public static String getHostIp()  {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.getDisplayName().contains("Gateway")  /////排除奇安新网关获取实际ip
                        || netInterface.getDisplayName().contains("VMware")) {/////排除虚拟网卡
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
            logger.error("获取IP信息异常:{}",e.getMessage());
        }
        return "";

    }


    public static void main(String[] args) {
        System.out.println("-------------------------");
        System.out.println(getHostIp());
        System.out.println("=====================");
    }


    public static int getHostId(String str, int max) {
        //中文字段过滤
        str = Pattern.compile("[\u4e00-\u9fa5]").matcher(str).replaceAll("-").trim();
        byte[] bytes = str.getBytes();
        int sums = 0;
        for (int b:bytes) {
            sums +=b;
        }
        return sums %(max +1);
    }
}
