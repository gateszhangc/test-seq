package com.dap.sequence.server.service.impl;

import com.base.core.service.impl.AbstractBaseServiceImpl;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.entity.SeqUseCondition;
import com.dap.sequence.server.service.SeqUseConditionService;
import com.dap.sequence.server.utils.SequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.dap.sequence.server.utils.EnvUtils.getAddress;


/**
 * @author: liu
 * @date: 2022/2/22
 * @description:
 */
@Service
public class SeqUseConditionServiceImpl extends AbstractBaseServiceImpl<SeqUseCondition, String> implements SeqUseConditionService {

    private static final Logger LOG = LoggerFactory.getLogger(SeqConsumerImpl.class);

    private static String hostAddress = null;

    @Value("${server.port}")
    private int port;

    static {
        try {
            hostAddress = getAddress();
            LOG.info("Host address: {}", hostAddress);
        } catch (SequenceException e) {
            LOG.error("获取本地地址报错.error:{}", e.getMessage(), e);
        }
    }

    @Override
    public void saveUseCondition(ArrayList<Object> obj, String sequenceCode, SeqDesignPo seqDesignPo, String clientInfo) {
        if (obj.isEmpty()) {
            LOG.error("序列[{}]数量异常", sequenceCode);
        }
        SeqUseCondition seqUseCondition = new SeqUseCondition();
        seqUseCondition.setClientInfo(clientInfo);
        seqUseCondition.setNumberStart(obj.get(0).toString());
        seqUseCondition.setNumberEnd(obj.get(obj.size() - 1).toString());
        seqUseCondition.setSeqNo(obj.size());
        seqUseCondition.setDesignId(seqDesignPo.getId());
        seqUseCondition.setTenantId(seqDesignPo.getTenantId());
        seqUseCondition.setSeqName(seqDesignPo.getSequenceName());
        seqUseCondition.setServerInfo(hostAddress + ":" + port);
        seqUseCondition.setId(SequenceUtil.getUUID32());
        this.saveObject(seqUseCondition);
    }
}
