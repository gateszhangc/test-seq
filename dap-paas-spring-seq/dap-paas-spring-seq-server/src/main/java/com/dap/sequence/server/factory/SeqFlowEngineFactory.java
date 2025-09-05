package com.dap.sequence.server.factory;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.core.SeqProducer;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.server.core.DefaultSeqFlowEngine;
import com.dap.sequence.server.core.SerialSeqProducer;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description: 核心引擎生产工厂类
 * @Author: liu
 * @Date: 2022/2/11
 */
@Component
public class SeqFlowEngineFactory {

    private static final Logger log = LoggerFactory.getLogger(SeqFlowEngineFactory.class);

    @Autowired
    private SeqDesignService seqDesignService;

    @Value("${dap.sequence.callbackEnable:true}")
    private boolean callbackEnable;

    /**
     * 工厂类负责生产 连续/非连续型 序列引擎
     *
     * @throws SequenceException
     */
    public SeqDesignPo doCreateEngine(String seqDesignId) throws SequenceException {

        Map map = new HashMap();
        map.put("id", seqDesignId);
        map.put("sequenceStatus", SequenceConstant.designStateOn);
        List<SeqDesignPo> seqDesignPos = Optional.ofNullable(seqDesignService.queryList(map))
                .orElse(Collections.emptyList());

        seqDesignPos.forEach(seq -> {
            //一个引擎对象绑定一个序列发布对象，在此处仅仅完成engine属性注入，创建引擎对象。
            AbstractSeqFlowEngine defaultSeqFlowEngine = new DefaultSeqFlowEngine();

            if (Objects.nonNull(seq)) {
                //连续性序列引擎构建
                AtomicReference<SeqProducer> seqProducerReference = new AtomicReference<>();
                SerialSeqProducer serialSeqProducer = new SerialSeqProducer();
                serialSeqProducer.setCallbackEnable(callbackEnable);
                seqProducerReference.set(serialSeqProducer);
                defaultSeqFlowEngine.setSeqProducer(seqProducerReference);
                defaultSeqFlowEngine.setSeqCode(seq.getSequenceCode());
                defaultSeqFlowEngine.setTenantId(seq.getTenantId());
                //完成引擎构建 将构建的引擎存储到集合中
                // 具体加载数据在每一个引擎对象的loadConf方法中具体体现。
                defaultSeqFlowEngine.refresh();
                SeqHolder.getEngineMap().put(seq.getSequenceCode()+SequenceConstant.TenantPostfix+seq.getTenantId(), defaultSeqFlowEngine);
                log.info("{}引擎加载完成，开始加载配置信息.....", seq.getSequenceName());
            } else {
                log.warn("{}找不到对应数据", seq.getSequenceName());
            }
        });
        if (seqDesignPos.size() == 0) {
            log.info("没有加载到任何数据！请检查配置！");
        }

        return (seqDesignPos!=null && seqDesignPos.size()>0)?seqDesignPos.get(0): new SeqDesignPo();
    }

}
