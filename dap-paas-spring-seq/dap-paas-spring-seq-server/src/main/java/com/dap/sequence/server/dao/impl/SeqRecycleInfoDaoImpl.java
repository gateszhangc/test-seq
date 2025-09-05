package com.dap.sequence.server.dao.impl;


import com.base.api.exception.DaoException;
import com.base.api.util.SqlSessionMapperLabel;
import com.base.core.dao.impl.AbstractBaseDaoImpl;
import com.dap.sequence.server.dao.SeqRecycleInfoDao;
import com.dap.sequence.server.entity.SeqRecycleInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/8/30 20:19
 * @description:
 */
@Repository
public class SeqRecycleInfoDaoImpl extends AbstractBaseDaoImpl<SeqRecycleInfo,String> implements SeqRecycleInfoDao {
    @Override
    public Integer insertBatch(List<SeqRecycleInfo> seqRecycleInfos) throws DaoException {
        int total = 0;
        try {
            total = this.getSqlSession().insert(getSqlNamespace() + ".insertBatch", seqRecycleInfos);
        }catch (Exception e){
            throw new DaoException("序列回收信息存储异常："+e.getMessage());
        }
        return total;
    }

    @Override
    public List<SeqRecycleInfo> getSeqForUpdate(Map map) throws DaoException{
        List<SeqRecycleInfo> result ;
        try {
            result= this.getSqlSession().selectList(getSqlNamespace()+".getSeqForUpdate", map);
        }catch (Exception e){
            throw new DaoException("getSeqForUpdate 查询异常："+e.getMessage());
        }
        return result;
    }
}
