package com.dap.sequence.server.service;


import com.base.api.exception.DaoException;
import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqRecycleInfo;

import java.util.List;
import java.util.Map;

/**
 * @author: liu
 * @date: 2022/8/30 20:21
 * @description:
 */
public interface SeqRecycleInfoService extends BaseService<SeqRecycleInfo,String> {

    Integer insertBatch(List<SeqRecycleInfo> list) throws DaoException;

    List<SeqRecycleInfo> getSeqForUpdate(Map map) throws DaoException;
}
