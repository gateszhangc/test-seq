package com.dap.sequence.server.service;

import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqCurNum;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/2/7 14:05
 * @Descricption:
 */
public interface SeqCurNumService extends BaseService<SeqCurNum, String> {
    SeqCurNum getObjectByCode(Map map);
}
