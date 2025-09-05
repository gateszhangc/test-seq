package com.dap.sequence.server.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置线程变量使用
 *
 * @author yzp
 * @date 2024/7/2
 */
public class ThreadLocalVariables {
    private static final ThreadLocal<Map<Object,Object>> THREAD_LOCAL =
            ThreadLocal.withInitial(HashMap::new);

    public static void set(Object key, Object value){
        THREAD_LOCAL.get().put(key,value);
    }

    public static Object get(Object key){
        return THREAD_LOCAL.get().get(key);
    }

    public static void unset(){
        THREAD_LOCAL.remove();
    }

    public static Map<Object,Object> get(){
        return THREAD_LOCAL.get();
    }
}
