package com.dap.sequence.server.service.impl;


import com.base.api.exception.DaoException;
import com.base.api.exception.ServiceException;
import com.base.core.service.impl.AbstractBaseServiceImpl;
import com.dap.sequence.server.dao.SeqCurNumDao;
import com.dap.sequence.server.dao.SeqServiceNodeDao;
import com.dap.sequence.server.entity.SeqServiceNode;
import com.dap.sequence.server.service.SeqServiceNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author scalor
 * @date 2022/1/18
 */
@Service
public class SeqServiceNodeServiceImpl extends AbstractBaseServiceImpl<SeqServiceNode, String> implements SeqServiceNodeService {

    @Autowired
    private SeqServiceNodeDao seqServiceNodeDao;
    @Override
    public SeqServiceNode getObjectByClusterName(String seqName) {
        try {
            return seqServiceNodeDao.getObjectByClusterName(seqName);
        } catch (DaoException e) {
            throw new ServiceException("getObjectByCode" + e.getMessage());
        }
    }
}
