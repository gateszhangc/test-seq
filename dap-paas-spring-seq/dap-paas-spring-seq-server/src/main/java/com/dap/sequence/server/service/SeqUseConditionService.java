package com.dap.sequence.server.service;

import java.util.ArrayList;

import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.entity.SeqUseCondition;

/**
 * @author: liu
 * @date: 2022/2/22
 * @description:
 */
public interface SeqUseConditionService extends BaseService<SeqUseCondition, String> {
    /**
     *@Description 存储序列生成信息
     *@param   obj 生成序列集合
     *@param   sequenceCode 序列编号
     *@date
     */
    public void saveUseCondition(ArrayList<Object> obj, String sequenceCode,SeqDesignPo seqDesignPo,  String clientInfo);
}
