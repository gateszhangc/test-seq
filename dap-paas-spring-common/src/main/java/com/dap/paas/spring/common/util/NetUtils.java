package com.dap.paas.spring.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class NetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NetUtils.class);

    private static final int TIME_OUT = 3000;

    /** 检查主机端口是否正常通信 */
    public static boolean telnet(String host, int port) {
    	LOG.info("telnet host:{} , port{}" , host, port);
        if (StringUtils.isEmpty(host) || 0 == port) {
            return false;
        }
        TelnetClient client = new TelnetClient();
        try {
        	client.setConnectTimeout(TIME_OUT);
            client.connect(host, port);
        } catch (IOException e) {
        	LOG.warn("telnet host:{} , port{} IOException:{}" , host, port , e.getMessage());
           // e.printStackTrace();
            return false;
        } finally {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /** Ping主机 */
    public static boolean ping(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(TIME_OUT);
        } catch (Exception e) {
            LOG.error("Ping [" + host + "] server has crash or not exist.", e);
            return false;
        }
    }

    /** 获取本地ip */
    public static String ip() {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Get local server ip has error", e);
        }
        return ip;
    }

}
