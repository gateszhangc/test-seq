package com.dap.sequence.server.holder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列服务句柄
 */
public class SeqServiceHolder {
    /**
     * 序列存储map @tendId
     */
    protected static ConcurrentHashMap<String, BlockingQueue<Object>>  tenantMap = new ConcurrentHashMap<>();

    /**
     * 获取存储对象
     * @return
     */
    public static ConcurrentHashMap<String, BlockingQueue<Object>> getTenantMap() {
        return tenantMap;
    }

}
