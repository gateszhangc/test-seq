package com.dap.sequence.client.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.SequenceException;
import com.dap.sequence.client.properties.CusSeq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zpj
 */
public class SequenceQueue {

    private static final Logger logger = LoggerFactory.getLogger(SequenceQueue.class);
    /**
     * 序列临时缓存
     */
    private volatile BlockingQueue<Object> cacheBlockingQueue = null;
    /**
     * 序列缓存集合
     */
    private volatile BlockingQueue<BlockingQueue<Object>> quenList = null;
    /**
     * 操作锁
     */
    private Boolean isNum;

    private Date lastGetTime;
    
    private int publishCount = 1000;

    // 从队列中获取序列的超时时间 默认2秒
    private long timeout = 2000;
    /**
     * 下一日的序列是否存在
     */
    private Boolean hasNextDaySeq = false;

    /**
     * 是否已清理本地序列缓存{@link SequenceQueueFactory#SEQUENCE_QUEUE_MAP}
     * 默认是false未清理，true已清理
     */
    private final AtomicBoolean hasClearSequenceQueueMap = new AtomicBoolean(false);

    public AtomicBoolean getHasClearSequenceQueueMap() {
        return hasClearSequenceQueueMap;
    }

    public boolean setHasClearSequenceQueueMap() {
        return hasClearSequenceQueueMap.getAndSet(true);
    }


    public Boolean getHasNextDaySeq() {
        return hasNextDaySeq;
    }

    public void setHasNextDaySeq(Boolean hasNextDaySeq) {
        this.hasNextDaySeq = hasNextDaySeq;
    }

    /**
     * 设置锁 保证一个序列只会执行一次
     * 第一个调用返回false 后面都返回true
     *
     * @return
     */
    public synchronized Boolean setNextDaySeqIfAbsent() {
        if (!hasNextDaySeq) {
            this.hasNextDaySeq = true;
            return false;
        }
        return true;
    }

    public SequenceQueue() {

    }

    public SequenceQueue(List<Object> list, int queueLength, long timeout) {
        this.timeout = timeout;
        this.quenList = new ArrayBlockingQueue<BlockingQueue<Object>>(queueLength);
        this.quenList.add(new ArrayBlockingQueue<Object>(list.size(), true, list));

    }

    public synchronized BlockingQueue<Object> getCacheBlockingQueue() {
            this.lastGetTime = new Date();
            if (this.cacheBlockingQueue == null || this.cacheBlockingQueue.isEmpty()) {
                cacheBlockingQueue = this.quenList.poll();
            }
            return cacheBlockingQueue;
    }


    public synchronized CusSeq getCacheSeq() {
        try {
            CusSeq cusSeq = new CusSeq();
            this.lastGetTime = new Date();
            if (this.cacheBlockingQueue == null || this.cacheBlockingQueue.isEmpty()) {
                cacheBlockingQueue = this.quenList.poll(timeout, TimeUnit.MILLISECONDS);
            }
            if (cacheBlockingQueue == null) {
                throw new RuntimeException("操作队列出错!");
            }
            cusSeq.setQueueSize(cacheBlockingQueue.size());
            cusSeq.setSeq(cacheBlockingQueue.poll(timeout, TimeUnit.MILLISECONDS));
            return cusSeq;
        } catch (InterruptedException e) {
            logger.error("从cacheBlockingQueue中poll序列异常", e);
            throw new SequenceException(ExceptionEnum.GET_SEQ_QUEUE_ERROR);
        }
    }

    public void setCacheBlockingQueue(BlockingQueue<Object> cacheBlockingQueue) {
        this.cacheBlockingQueue = cacheBlockingQueue;
    }

    public BlockingQueue<BlockingQueue<Object>> getQuenList() {
        return this.quenList;
    }

    public void setQuenList(BlockingQueue<BlockingQueue<Object>> quenList) {
        this.quenList = quenList;
    }

    public Boolean getNum() {
        return isNum;
    }

    public void setNum(Boolean num) {
        isNum = num;
    }

    public int getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(int publishCount) {
        this.publishCount = publishCount;
    }

	public Date getLastGetTime() {
		return lastGetTime;
	}

	public void setLastGetTime(Date lastGetTime) {
		this.lastGetTime = lastGetTime;
	}
	
    public BlockingQueue<Object> getBufferQueue() {
        return this.cacheBlockingQueue;
    }

}
