package com.dap.sequence.server.service.impl;


import com.base.api.exception.DaoException;
import com.base.core.service.impl.AbstractBaseServiceImpl;
import com.dap.sequence.server.dao.SeqRecycleInfoDao;
import com.dap.sequence.server.entity.SeqRecycleInfo;
import com.dap.sequence.server.service.SeqRecycleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/8/30 20:22
 * @description:
 */
@Service
public class SeqRecycleInfoServiceImpl extends AbstractBaseServiceImpl<SeqRecycleInfo,String> implements SeqRecycleInfoService {
  @Autowired
  private SeqRecycleInfoDao seqRecycleInfoDao;
    @Override
    public Integer insertBatch(List<SeqRecycleInfo> list) throws DaoException {
        return seqRecycleInfoDao.insertBatch(list);
    }

  @Override
  public List<SeqRecycleInfo> getSeqForUpdate(Map map) throws DaoException {
    return seqRecycleInfoDao.getSeqForUpdate(map);
  }
}
