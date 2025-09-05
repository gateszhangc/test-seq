package com.dap.sequence.server.service;

import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqInstanceRule;

import java.util.List;
import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/1/20 9:25
 * @Descricption:
 */
public interface SeqInstanceRuleService extends BaseService<SeqInstanceRule, String> {
    List<SeqInstanceRule> getInstanceRuleList(Map map);
}
