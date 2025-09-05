package com.dap.sequence.api.lock;

import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.SequenceException;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className LockImpl
 * @description 锁实现
 * @author renle
 * @date 2023/11/30 14:04:44 
 * @version: V23.06
 */
public class LockImpl<T> implements ILock<T> {


    private final Map<T, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public void lock(T key) {

        if (key == null) {
            throw new SequenceException(ExceptionEnum.LOCK_KEY_EMPTY);
        }

        // 获取或创建一个ReentrantLock对象
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());

        // 加锁
        lock.lock();
    }

    @Override
    public boolean tryLock(T key) {
        if (key == null) {
            throw new SequenceException(ExceptionEnum.LOCK_KEY_EMPTY);
        }

        // 获取或创建一个ReentrantLock对象
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());

        // 加锁
        return lock.tryLock();
    }

    @Override
    public void unlock(T key) {

        // 如果key为空，直接返回
        if (key == null) {
            throw new SequenceException(ExceptionEnum.LOCK_KEY_EMPTY);
        }

        // 从Map中获取锁对象
        ReentrantLock lock = lockMap.get(key);

        // 获取不到报错
        if (lock == null) {
            throw new SequenceException(ExceptionEnum.LOCK_NOT_ADD_KEY);
        }

        // 其他线程非法持有不允许释放
        if (!lock.isHeldByCurrentThread()) {
            throw new SequenceException(ExceptionEnum.LOCK_NOT_HAVE_THREAD);
        }

        // 释放锁
        lock.unlock();
    }

    @Override
    public boolean isLocked(T key) {

        // 如果key为空，直接返回
        if (key == null) {
            throw new SequenceException(ExceptionEnum.LOCK_KEY_EMPTY);
        }

        // 从Map中获取锁对象
        ReentrantLock lock = lockMap.get(key);

        return lock != null && lock.isLocked();
    }
}
