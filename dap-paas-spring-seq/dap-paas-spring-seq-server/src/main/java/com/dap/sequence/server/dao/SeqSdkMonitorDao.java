package com.dap.sequence.server.dao;

import com.base.core.dao.BaseDao;
import com.dap.sequence.api.dto.SnowflakeRcvCluster;
import com.dap.sequence.server.entity.SeqSdkMonitor;

import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/2/24 10:46
 * @description:
 */
public interface SeqSdkMonitorDao extends BaseDao<SeqSdkMonitor, String> {

    /**
     * 更新
     *
     * @param seqSdkMonitor
     * @return
     */
    int updateAllInfoWithVersion(SeqSdkMonitor seqSdkMonitor);

    /**
     * 查询
     *
     * @param paramMap
     * @return
     */
    List<SnowflakeRcvCluster> queryClusterForRcv(Map<String, Object> paramMap);


    /**
     * 查询workId
     *
     * @param paramMap
     * @return
     */
    Integer queryMaxWorkId(Map<String, Object> paramMap);

    /**
     * 更新workId
     *
     * @param paramMap
     * @return
     */
    int updateStatus(Map<String, Object> paramMap);

}
