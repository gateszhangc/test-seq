package com.dap.sequence.server.dao;


import com.base.api.exception.DaoException;
import com.base.core.dao.BaseDao;
import com.dap.sequence.server.entity.SeqRecycleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/8/30 20:18
 * @description:
 */
public interface SeqRecycleInfoDao extends BaseDao<SeqRecycleInfo,String> {

    Integer insertBatch(@Param("list") List<SeqRecycleInfo> seqRecycleInfos) throws DaoException;

    List<SeqRecycleInfo> getSeqForUpdate(Map map) throws DaoException;
}
