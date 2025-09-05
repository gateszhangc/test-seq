package com.dap.sequence.server.service;

import com.base.api.exception.DaoException;
import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqSdkMonitor;
/**
 * 雪花算法-维护workId
 *
 * @author: liu
 * @date: 2022/2/24 10:47
 * @description:
 */
public interface SeqSdkMonitorService extends BaseService<SeqSdkMonitor, String> {

    /**
     * 序列客户端初始化workId
     *
     * @param seqSdkMonitor
     * @return
     */
    SeqSdkMonitor initWorkId(SeqSdkMonitor seqSdkMonitor) throws DaoException;

    /**
     * 通过心跳维护workId
     *
     * @param seqSdkMonitor
     * @return
     */
    boolean liveWorkId(SeqSdkMonitor seqSdkMonitor) throws DaoException;

    /**
     * 回收长时间未心跳的workId
     *
     * @param intervalTime 集群回收时，心跳最大间隔时间
     * @return
     */
    int rcvSnowflakeWorkId(int intervalTime);

    /**
     * 手动回收接口
     *
     * @param paramWorkData
     * @return
     */
    int rcvWorIdByClient(SeqSdkMonitor paramWorkData);
}