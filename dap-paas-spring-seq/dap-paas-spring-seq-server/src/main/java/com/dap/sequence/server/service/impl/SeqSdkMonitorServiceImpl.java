package com.dap.sequence.server.service.impl;

import com.base.api.exception.DaoException;
import com.base.core.service.impl.AbstractBaseServiceImpl;
import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.SequenceException;
import com.dap.sequence.server.dao.SeqSdkMonitorDao;
import com.dap.sequence.server.entity.SeqSdkMonitor;
import com.dap.sequence.server.service.SeqSdkMonitorService;
import com.dap.sequence.server.utils.UidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/2/24 10:48
 * @description:
 */
@Service
public class SeqSdkMonitorServiceImpl extends AbstractBaseServiceImpl<SeqSdkMonitor, String> implements SeqSdkMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeqSdkMonitorServiceImpl.class);

    @Autowired
    private SeqSdkMonitorDao seqSdkMonitorDao;

    /**
     * 正常情况下，处于运行中
     */
    private static final String MONITOR_STATUS_RUNNING = "1";

    /**
     * 超过一小时停止
     */
    private static final String MONITOR_STATUS_STOP = "0";

    /**
     * 最大的workId
     */
    private static final int MAX_WORK_ID = 1024;

    /**
     * 初始workId
     */
    private static final int MIN_WORK_ID = 0;

    /**
     * 默认版本
     */
    private static final int DEFAULT_VERSION = 1;

    /**
     * 集群信息
     */
    private static final String SDK_NAME = "sdkName";

    /**
     * instanceName
     */
    private static final String INSTANCE_NAME = "instanceName";

    /**
     * instanceName
     */
    private static final String STATUS = "status";


    /**
     * 处理client获取workId
     *
     * @param seqSdkMonitor
     * @return
     */
    @Override
    public SeqSdkMonitor initWorkId(SeqSdkMonitor seqSdkMonitor) throws DaoException {
        Map<String, Object> maxMap = new HashMap<>();
        maxMap.put(SDK_NAME, seqSdkMonitor.getSdkName());
        Integer maxWorkId = seqSdkMonitorDao.queryMaxWorkId(maxMap);
        //相同sdkName下没有workId数据，运用乐观锁循环新增
        if (maxWorkId == null) {
            seqSdkMonitor.setId(UidUtil.uid());
            seqSdkMonitor.setStatus(MONITOR_STATUS_RUNNING);
            seqSdkMonitor.setCreateDate(new Date());
            seqSdkMonitor.setUpdateDate(new Date());
            seqSdkMonitor.setVersion(DEFAULT_VERSION);
            //循环插入
            if (loopAddWorkId(seqSdkMonitor, MIN_WORK_ID)){
                return seqSdkMonitor;
            }
            LOGGER.error("获取workId失败，sequence client信息为IP: {{}}, Port: {{}}, InstanceName: {{}},sdkName: {{}},",
                    seqSdkMonitor.getHostIp(), seqSdkMonitor.getPort(), seqSdkMonitor.getInstanceName(), seqSdkMonitor.getSdkName());
            //超过最大值1024抛出异常
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_ADD_FAILURE);
        }

        //根据instanceName和 sdkName更新，更新成功，证明被使用
        Map<String, Object> oldWorkMap = new HashMap<>();
        oldWorkMap.put(SDK_NAME, seqSdkMonitor.getSdkName());
        oldWorkMap.put(INSTANCE_NAME, seqSdkMonitor.getInstanceName());
        oldWorkMap.put("updateDate", new Date());
        oldWorkMap.put(STATUS, MONITOR_STATUS_RUNNING);
        if(seqSdkMonitorDao.updateStatus(oldWorkMap)>0){
            List<SeqSdkMonitor> oldWorkId = seqSdkMonitorDao.queryList(oldWorkMap);
            return oldWorkId.get(0);
        }

        //取删除workId,进行重用
        Map<String, Object> stopMap = new HashMap<>();
        stopMap.put(SDK_NAME, seqSdkMonitor.getSdkName());
        stopMap.put(STATUS, MONITOR_STATUS_STOP);
        final List<SeqSdkMonitor> delWorkList = seqSdkMonitorDao.queryList(stopMap);
        if (!CollectionUtils.isEmpty(delWorkList)) {
            for (SeqSdkMonitor delWorkData : delWorkList) {
                seqSdkMonitor.setId(delWorkData.getId());
                seqSdkMonitor.setVersion(delWorkData.getVersion());
                seqSdkMonitor.setUpdateDate(new Date());
                seqSdkMonitor.setStatus(MONITOR_STATUS_RUNNING);
                //更新成功
                if (seqSdkMonitorDao.updateAllInfoWithVersion(seqSdkMonitor) > 0) {
                    seqSdkMonitor.setWorkId(delWorkData.getWorkId());
                    return seqSdkMonitor;
                }
            }
        }

        seqSdkMonitor.setId(UidUtil.uid());
        seqSdkMonitor.setUpdateDate(new Date());
        seqSdkMonitor.setCreateDate(new Date());
        seqSdkMonitor.setStatus(MONITOR_STATUS_RUNNING);
        seqSdkMonitor.setVersion(DEFAULT_VERSION);
        maxWorkId = seqSdkMonitorDao.queryMaxWorkId(maxMap);
        int nextWorkId = maxWorkId + 1;
        //运用乐观锁循环新增
        if (loopAddWorkId(seqSdkMonitor, nextWorkId)){
            return seqSdkMonitor;
        }
        LOGGER.error("获取workId失败，sequence client信息为IP: {{}}, Port: {{}}, InstanceName: {{}},sdkName: {{}},",
                seqSdkMonitor.getHostIp(), seqSdkMonitor.getPort(), seqSdkMonitor.getInstanceName(), seqSdkMonitor.getSdkName());
        //超过最大值1024抛出异常
        throw new SequenceException(ExceptionEnum.SEQ_SNOW_ADD_FAILURE);
    }

    /**
     * 运用乐观锁循环新增workId信息
     *
     * @param seqSdkMonitor
     * @param workId
     * @return
     */
    private boolean loopAddWorkId(SeqSdkMonitor seqSdkMonitor, int workId) {
        Map<String, Object> maxMap = new HashMap<>();
        maxMap.put(SDK_NAME, seqSdkMonitor.getSdkName());
        while (workId < MAX_WORK_ID) {
            seqSdkMonitor.setWorkId(workId);
            try {
                final Integer addCount = seqSdkMonitorDao.saveObject(seqSdkMonitor);
                if (addCount > 0) {
                    return true;
                }
            } catch (Exception e) {
                LOGGER.error("seq client信息为IP: {{}}, Port: {{}}, InstanceName: {{}},WorkId: {{}}, 插入撞库, 错误信息：",
                        seqSdkMonitor.getHostIp(), seqSdkMonitor.getPort(), seqSdkMonitor.getInstanceName(), workId);
            }
            //获取当前最大ID
            workId = seqSdkMonitorDao.queryMaxWorkId(maxMap);
            workId++;
        }
        return false;
    }


    /**
     * 通过心跳维护workId
     *
     * @param seqSdkMonitor
     * @return
     */
    @Override
    public boolean liveWorkId(SeqSdkMonitor seqSdkMonitor) throws DaoException {
        Map<String, Object> map = new HashMap<>();
        map.put(INSTANCE_NAME, seqSdkMonitor.getInstanceName());
        map.put("workId", seqSdkMonitor.getWorkId());
        map.put(SDK_NAME, seqSdkMonitor.getSdkName());
        List<SeqSdkMonitor> seqSdkMonitors = seqSdkMonitorDao.queryList(map);
        //维护workid心跳
        if (!CollectionUtils.isEmpty(seqSdkMonitors)) {
            for (SeqSdkMonitor curWorkData : seqSdkMonitors) {
                SeqSdkMonitor updateWorkData = new SeqSdkMonitor();
                updateWorkData.setId(curWorkData.getId());
                updateWorkData.setUpdateDate(new Date());
                updateWorkData.setStatus(MONITOR_STATUS_RUNNING);
                seqSdkMonitorDao.updateAllInfoWithVersion(updateWorkData);
            }
            return true;
        }
        return false;
    }

    /**
     * 回收长时间未心跳的workId
     *
     * @param intervalTime 集群回收时，心跳最大间隔时间
     * @return
     */
    @Override
    public int rcvSnowflakeWorkId(int intervalTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("updateDate", new Date());
        //计算回收时间
        Date rcvDate = new Date(System.currentTimeMillis() -intervalTime * 1000L);
        param.put("rcvDate", rcvDate);
        param.put(STATUS, MONITOR_STATUS_STOP);
        param.put("oldStatus", MONITOR_STATUS_RUNNING);
        return seqSdkMonitorDao.updateStatus(param);
    }

    /**
     * 手动回收接口
     *
     * @param paramWorkData
     * @return
     */
    @Override
    public int rcvWorIdByClient(SeqSdkMonitor paramWorkData) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(INSTANCE_NAME, paramWorkData.getInstanceName());
        paramMap.put("workId", paramWorkData.getWorkId());
        paramMap.put(SDK_NAME, paramWorkData.getSdkName());
        List<SeqSdkMonitor> seqSdkMonitorList = null;
        try {
            seqSdkMonitorList = seqSdkMonitorDao.queryList(paramMap);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(seqSdkMonitorList)) {
            return 0;
        }
        int totalCount = 0;
        //最大等待毫秒
        for (SeqSdkMonitor seqSdkMonitor : seqSdkMonitorList) {
            SeqSdkMonitor updateWorkData = new SeqSdkMonitor();
            updateWorkData.setId(seqSdkMonitor.getId());
            updateWorkData.setUpdateDate(new Date());
            updateWorkData.setStatus(MONITOR_STATUS_STOP);
            final int delCount = seqSdkMonitorDao.updateAllInfoWithVersion(updateWorkData);
            if (delCount > 0) {
                totalCount++;
            }
        }
        return totalCount;
    }

}
