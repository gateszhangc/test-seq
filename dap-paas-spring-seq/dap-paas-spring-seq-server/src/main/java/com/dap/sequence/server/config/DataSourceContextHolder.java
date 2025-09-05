package com.dap.sequence.server.config;

/**
 * @Author: cao
 * Data: 2022/2/28 16:24
 * @Descricption: 维护一个全局唯一的map来实现数据源的动态切换
 */
public class DataSourceContextHolder {

    private  static String dataType;

    public static synchronized void setDBType(String data) {
        dataType=data;
    }

    public static String getDBType() {
        return dataType;
    }

}
