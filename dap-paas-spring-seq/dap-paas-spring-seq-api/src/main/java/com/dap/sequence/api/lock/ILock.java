package com.dap.sequence.api.lock;

/**
 * @className ILock
 * @description ILock接口
 * @author renle
 * @date 2023/11/30 14:02:42 
 * @version: V23.06
 */
public interface ILock<T> {

    /**
     * 加锁
     *
     * @param key key
     */
    void lock(T key);

    /**
     * 加锁
     *
     * @param key key
     */
    boolean tryLock(T key);

    /**
     * 解锁
     *
     * @param key key
     */
    void unlock(T key);

    /**
     * 是否加锁
     *
     * @param key key
     */
    boolean isLocked(T key);
}
