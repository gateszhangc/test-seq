package com.dap.sequence.client.utils;

public class OtherUtil {

    //获取最新连接
    public static final String TYPE_1 = "urls";

    // SDK 发送监控数据到 server端地址
    public static final String MONITOR_SDK_URL = "/v1/seq/publish/insert/sdk";
    // 序列模板获取方法
    public static final String SEQ_CONSUMER_URL = "/v1/seq/publish/getStringSeq";
    // SDK 请求server端 获取所有 Server 的地址服务器信息
    public static final String GET_URL = "/v1/seq/publish/getStringIps";
    // SDK 请求server端 获取所有 序列规则信息
    public static final String GET_SEQ_DESIGN_OBJ = "/v1/seq/publish/getSeqDesignObj";

    // SDK 请求server端 获取所有 序列规则信息
    public static final String CHECK_ACCESSKEY = "/v1/seq/publish/checkAccessKey";

    public static String DEFULT_URLS = "";


    // SDK 服务端回收客户端缓存的数据
    public static final String CacheCallBack_URL = "/v1/seq/callback/shutdownCallbackSeq";

    public static final String HTTP = "http://";
    public static int PORT = 0;

    /**
     * 客户端启动时，向序列中心申请workId
     */
    public static final String SNOWFLAKE_INIT_WORK_ID = "/v1/seq/publish/snowflakewordid";

    /**
     * 客户端启动后，维护心跳
     */
    public static final String SNOWFLAKE_HEART_BEAT = "/v1/seq/publish/heartbeat";

}
