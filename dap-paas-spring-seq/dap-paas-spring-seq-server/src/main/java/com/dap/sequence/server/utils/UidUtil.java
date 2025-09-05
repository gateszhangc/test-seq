package com.dap.sequence.server.utils;

import java.util.UUID;

/**
 * 获取uuid
 *
 * @author lyf
 * @date 2024/1/21
 */
public class UidUtil {

    private UidUtil(){

    }

    /**
     * 获取 uuid
     * @return
     */
    public static String uid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
