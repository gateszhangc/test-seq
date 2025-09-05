package com.dap.sequence.server.service;

import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqDesignPo;

import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/1/19 14:01
 * @Descricption:
 */
public interface SeqDesignService extends BaseService<SeqDesignPo, String> {
    SeqDesignPo getObjectByCode(Map map);

    boolean checkAccessKey(Map map);
    /**
     * 根据唯一联合索引字段 seq_code、tenant_id 设置行级锁
     *
     * @param map
     * @author yzp
     * @return 序列设计信息
     */
    SeqDesignPo selectForUpdateBySeqCodeTenantTd(Map map);
}
