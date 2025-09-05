package com.dap.sequence.server.core;

import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.utils.SeqTypeAnalyser;
import com.dap.sequence.server.utils.SequenceUtil;
import com.dap.sequence.server.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description: 默认处理引擎
 * @Author: liu
 * @Date: 2022/2/11
 */
public class DefaultSeqFlowEngine extends AbstractSeqFlowEngine {

    private static final Logger log = LoggerFactory.getLogger(DefaultSeqFlowEngine.class);

    private SeqDesignService seqDesignService = SpringUtils.getApplicationContext().getBean(SeqDesignService.class);

    /**
     * 对应唯一的引擎加载所需的序列配置
     *
     * @return
     */

    @Override
    public void loadConf() {
        SeqObj seqObj = new SeqObj();
        Map<String, String> map = new HashMap<>();
        map.put("seqCode", this.getSeqCode());
        map.put("sequenceStatus", SequenceConstant.designStateOn);
        SeqDesignPo seqDesignPo = seqDesignService.getObjectByCode(map);
        BeanUtils.copyProperties(seqDesignPo, seqObj);
        try {
            seqObj.setRuleInfos(SeqHolder.getRulesMap().get(seqObj.getId()));
        } catch (Exception e) {
            return;
        }
        this.setSeqObj(seqObj);
        //seqObj.setNum(SeqTypeAnalyser.checkNum(SequenceUtil.testSequence(seqObj)));
        log.info("{}配置加载完成!!", seqObj.getSequenceName());
    }

}
