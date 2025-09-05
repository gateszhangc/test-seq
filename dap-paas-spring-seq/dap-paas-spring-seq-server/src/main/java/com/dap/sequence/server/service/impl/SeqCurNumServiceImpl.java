package com.dap.sequence.server.service.impl;

import com.base.api.exception.DaoException;
import com.base.api.exception.ServiceException;
import com.base.core.service.impl.AbstractBaseServiceImpl;
import com.dap.sequence.server.dao.SeqCurNumDao;
import com.dap.sequence.server.entity.SeqCurNum;
import com.dap.sequence.server.service.SeqCurNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/2/7 14:57
 * @Descricption:
 */
@Service
public class SeqCurNumServiceImpl
        extends AbstractBaseServiceImpl<SeqCurNum, String> implements SeqCurNumService {

    @Autowired
    private SeqCurNumDao seqCurNumDao;

    @Override
    public SeqCurNum getObjectByCode(Map map) {
        try {
            return seqCurNumDao.getObjectByCode(map);
        } catch (DaoException e) {
            throw new ServiceException("getObjectByCode" + e.getMessage());
        }
    }
}
