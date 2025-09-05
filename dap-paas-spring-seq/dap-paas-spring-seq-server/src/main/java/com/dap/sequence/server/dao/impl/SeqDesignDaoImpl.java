package com.dap.sequence.server.dao.impl;

import com.base.api.exception.DaoException;
import com.base.api.util.SqlSessionMapperLabel;
import org.springframework.stereotype.Repository;

import com.base.core.dao.impl.AbstractBaseDaoImpl;
import com.dap.sequence.server.dao.SeqDesignDao;
import com.dap.sequence.server.entity.SeqDesignPo;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/1/19 14:00
 * @Descricption:
 */
@Repository
public class SeqDesignDaoImpl extends AbstractBaseDaoImpl<SeqDesignPo,String> implements SeqDesignDao {
    @Override
    public SeqDesignPo getObjectByCode(Map map) throws DaoException {
        SeqDesignPo result=null;
        try {
            result= this.getSqlSession().selectOne(getSqlNamespace()+".getObjectByCode", map);
        }catch (Exception e){
            String msg= SqlSessionMapperLabel.GetObjectById.getName()+":getObjectByCode "+e.getMessage();
            throw new DaoException(msg);
        }
        return result;
    }

    @Override
    public int checkAccessKey(Map map)  {
        return this.getSqlSession().selectOne(getSqlNamespace()+".checkAccessKey",map);
    }


    @Override
    public SeqDesignPo selectForUpdateBySeqCodeTenantTd(Map map) {
        return this.getSqlSession().selectOne(getSqlNamespace() + ".selectForUpdateBySeqCodeTenantTd", map);
    }
}
