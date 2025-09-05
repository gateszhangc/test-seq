package com.dap.sequence.server.dao;

import com.base.api.exception.DaoException;
import com.base.core.dao.BaseDao;
import com.dap.sequence.server.entity.SeqDesignPo;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/1/19 13:59
 * @Descricption:
 */
public interface SeqDesignDao extends BaseDao<SeqDesignPo, String> {
    SeqDesignPo getObjectByCode(Map map)throws DaoException;

    int checkAccessKey(Map map) ;
    /**
     * 根据序列设计表唯一联合索引seq_code、tenant_id 实现分布式锁
     *
     * @param map
     * @return
     */
    SeqDesignPo selectForUpdateBySeqCodeTenantTd(Map map);
}
