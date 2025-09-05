package com.dap.sequence.server.dao.impl;

import com.base.api.exception.DaoException;
import com.base.api.util.SqlSessionMapperLabel;
import com.base.core.dao.impl.AbstractBaseDaoImpl;
import com.dap.sequence.server.dao.SeqInstanceRuleDao;
import com.dap.sequence.server.entity.SeqInstanceRule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/1/20 9:19
 * @Descricption:
 */
@Repository
public class SeqInstanceRuleDaoImpl extends AbstractBaseDaoImpl<SeqInstanceRule, String> implements SeqInstanceRuleDao {
    @Override
    public List<SeqInstanceRule> getInstanceRuleList(Map map) throws DaoException {
        List result;
        try {
            result = this.getSqlSession().selectList(getSqlNamespace() + ".InstanceRuleList", map);
        } catch (Exception e) {
            String msg = SqlSessionMapperLabel.QueryList.getName() + ":InstanceRuleList " + e.getMessage();
            throw new DaoException(msg);
        }
        return result;
    }
}
