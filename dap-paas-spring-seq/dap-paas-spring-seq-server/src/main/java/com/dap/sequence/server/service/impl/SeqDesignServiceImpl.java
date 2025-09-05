package com.dap.sequence.server.service.impl;

import com.base.api.exception.DaoException;
import com.base.api.exception.ServiceException;
import com.base.core.service.impl.AbstractBaseServiceImpl;
import com.dap.sequence.server.dao.SeqDesignDao;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.service.SeqDesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/1/19 14:02
 * @Descricption:
 */
@Service
public class SeqDesignServiceImpl extends AbstractBaseServiceImpl<SeqDesignPo, String> implements SeqDesignService {

    @Autowired
    private SeqDesignDao seqDesignDao;

    @Override
    public SeqDesignPo getObjectByCode(Map map) {
        try {
            return seqDesignDao.getObjectByCode(map);
        } catch (DaoException e) {
            throw new ServiceException("getObjectByCode" + e.getMessage());
        }
    }

    @Override
    public boolean checkAccessKey(Map map) {
        int i = seqDesignDao.checkAccessKey(map);
        return i>0?true:false;
    }
    @Override
    public SeqDesignPo selectForUpdateBySeqCodeTenantTd(Map map) {
        return seqDesignDao.selectForUpdateBySeqCodeTenantTd(map);
    }
}
