package com.dap.sequence.server.dao;

import com.base.api.exception.DaoException;
import com.base.core.dao.BaseDao;
import com.dap.sequence.server.entity.SeqCurNum;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/2/7 13:50
 * @Descricption:
 */
public interface SeqCurNumDao extends BaseDao<SeqCurNum, String> {
    SeqCurNum getObjectByCode(Map map) throws DaoException;
}
