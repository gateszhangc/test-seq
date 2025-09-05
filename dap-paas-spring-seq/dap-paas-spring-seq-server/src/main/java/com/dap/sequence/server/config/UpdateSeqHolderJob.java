package com.dap.sequence.server.config;

import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.SeqFlowEngine;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.factory.SeqFlowEngineFactory;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/3/21 9:10
 * @Descricption:定时更新缓存
 */
//@Component
public class UpdateSeqHolderJob {

    private static final Logger logger = LoggerFactory.getLogger(UpdateSeqHolderJob.class);



 

}
