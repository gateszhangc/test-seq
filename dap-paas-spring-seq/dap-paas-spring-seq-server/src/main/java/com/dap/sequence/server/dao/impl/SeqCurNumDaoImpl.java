package com.dap.sequence.server.dao.impl;

import com.base.api.exception.DaoException;
import com.base.api.util.SqlSessionMapperLabel;
import com.base.core.dao.impl.AbstractBaseDaoImpl;
import com.dap.sequence.server.dao.SeqCurNumDao;
import com.dap.sequence.server.entity.SeqCurNum;
import com.dap.sequence.server.entity.SeqDesignPo;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/2/7 13:53
 * @Descricption:
 */
@Repository
public class SeqCurNumDaoImpl
        extends AbstractBaseDaoImpl<SeqCurNum, String> implements SeqCurNumDao {
    @Override
    public SeqCurNum getObjectByCode(Map map) throws DaoException {
        SeqCurNum result=null;
        try {
            result= this.getSqlSession().selectOne(getSqlNamespace()+".getObjectByCode", map);
        }catch (Exception e){
            String msg= SqlSessionMapperLabel.GetObjectById.getName()+":getObjectByCode "+e.getMessage();
            throw new DaoException(msg);
        }
        return result;
    }
}
