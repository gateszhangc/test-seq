package com.dap.sequence.server.dao.impl;

import com.base.core.dao.impl.AbstractBaseDaoImpl;
import com.dap.sequence.api.dto.SnowflakeRcvCluster;
import com.dap.sequence.server.dao.SeqSdkMonitorDao;
import com.dap.sequence.server.entity.SeqSdkMonitor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/2/24 10:46
 * @description:
 */
@Repository
public class SeqSdkMonitorDaoImpl extends AbstractBaseDaoImpl<SeqSdkMonitor, String> implements SeqSdkMonitorDao {

    /**
     * 更新全部信息
     *
     * @param seqSdkMonitor
     * @return
     */
    @Override
    public int updateAllInfoWithVersion(SeqSdkMonitor seqSdkMonitor) {
        return this.getSqlSession().update(getSqlNamespace().concat(".updateAllInfoWithVersion"), seqSdkMonitor);
    }

    @Override
    public List<SnowflakeRcvCluster> queryClusterForRcv(Map<String, Object> paramMap) {
        return this.getSqlSession().selectList(getSqlNamespace().concat(".queryClusterForRcv"), paramMap);
    }

    /**
     * 查询最大的workId
     *
     * @param paramMap
     * @return
     */
    @Override
    public Integer queryMaxWorkId(Map<String, Object> paramMap) {
        return this.getSqlSession().selectOne(getSqlNamespace().concat(".queryMaxWorkId"), paramMap);
    }

    /**
     * 回收workId
     *
     * @param paramMap
     * @return
     */
    @Override
    public int updateStatus(Map<String, Object> paramMap) {
        return this.getSqlSession().update(getSqlNamespace().concat(".updateStatus"), paramMap);
    }
}
