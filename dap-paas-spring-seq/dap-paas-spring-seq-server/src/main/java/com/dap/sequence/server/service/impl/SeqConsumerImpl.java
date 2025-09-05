package com.dap.sequence.server.service.impl;

import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.core.SeqProducer;
import com.dap.sequence.api.dto.SeqCallbackDto;
import com.dap.sequence.api.dto.SeqTransferDto;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.api.lock.LockImpl;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.server.core.AsyncTask;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.factory.SeqFlowEngineFactory;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.utils.RandomUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Description: 消费接口服务端实现
 * @Author: zpj
 * @Date: 2021/2/22 17:10
 * @Version: 1.0.1
 */
@Component
public class SeqConsumerImpl implements SeqConsumer {

    private static final Logger log = LoggerFactory.getLogger(SeqConsumerImpl.class);

    private final LockImpl<String> lockByKey = new LockImpl<>();

    @Autowired
    private SeqFlowEngineFactory seqFlowEngineFactory;

    @Autowired
    private SeqDesignService seqDesignService;

    @Resource
    private AsyncTask asyncTask;


    @Value("${dap.sequence.retryCount:5}")
    private int retryCount;

    @Override
    public SeqTransferDto getSeqFormServer(String sequenceCode, String tenantId) {

        //规则中配置的序列号
        String seqCode = StringUtils.substringBefore(sequenceCode, SequenceConstant.DAY_SWITCH_SPLIT);
        if (!sequenceCodeValidation(seqCode, tenantId)) {
            return new SeqTransferDto();
        }
        String key = seqCode + SequenceConstant.TenantPostfix + tenantId;
        AbstractSeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(key);
        if (seqFlowEngine == null) {
            log.error("租户号码：{}  编号 {} 序列未生成!!! ", tenantId, sequenceCode);
            return new SeqTransferDto();
        }

        SeqTransferDto transferDto = new SeqTransferDto();
        transferDto.setSequenceCode(sequenceCode);
        transferDto.setList(doGetSeq(sequenceCode, tenantId));
        transferDto.setNum(seqFlowEngine.getSeqObj().isNum());
        // 异步清理缓存序列
        asyncTask.clearSequenceMap(sequenceCode,tenantId);
        return transferDto;
    }

    private ArrayList<Object> doGetSeq(String sequenceCode, String tenantId) {
        ArrayList<Object> cacheList = null;

        //拼接序列格式
        String[] seqArray = sequenceCode.split(SequenceConstant.DAY_SWITCH_SPLIT);
        String seqCode = seqArray[0];
        String day;
        if (seqArray.length == 2) {
            day = seqArray[1];
        } else {
            day = SequenceConstant.DAY_BASE;
        }

        AbstractSeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(seqCode + SequenceConstant.TenantPostfix + tenantId);
        SeqProducer seqProducer = seqFlowEngine.getSeqProducer().get();
        String key = sequenceCode + SequenceConstant.TenantPostfix + tenantId;
        try {
            BlockingQueue<Object> unUseQueue = SeqHolder.getSequenceMapBySequenceCode(key);
            int generatedSeqSize = getGeneratedSeqSize(seqFlowEngine.getSeqObj().getSequenceNumber());
            if (unUseQueue != null && unUseQueue.size() >= generatedSeqSize) {
                //直接拿
                cacheList = new ArrayList<>();
                unUseQueue.drainTo(cacheList, generatedSeqSize);

                //异步生产序列
                seqFlowEngine.getSeqObj().setTenantId(tenantId);
                asyncTask.createSeq(seqProducer,seqFlowEngine.getSeqObj(), day,key);
            } else {
                //尝试3次
                for (int i = 0; i < retryCount; i++) {
                    seqFlowEngine.getSeqObj().setTenantId(tenantId);
                    //不够用先生产
                    seqProducer.createSeq(seqFlowEngine.getSeqObj(), day);
                    seqFlowEngine.getSeqObj().setTenantId(tenantId);

                    BlockingQueue<Object> queue = Optional.ofNullable(SeqHolder.getSequenceMapBySequenceCode(key)).orElseGet(LinkedBlockingQueue::new);

                    //生产完成后
                    if (queue.size() >= generatedSeqSize) {
                        cacheList = new ArrayList<>(seqFlowEngine.getSeqObj().getSequenceNumber());
                        queue.drainTo(cacheList,generatedSeqSize);
                        //成功后再次异步生产序列放入缓存
                        seqFlowEngine.getSeqObj().setTenantId(tenantId);
                        asyncTask.createSeq(seqProducer,seqFlowEngine.getSeqObj(), day, key);
                        break;
                    } else {
                        log.warn("序列SeqCode：{} , 第{}次生成序列失败", sequenceCode, i + 1);
                    }
                }
            }

            String start;
            String end;
            int size;
            if (cacheList == null || cacheList.isEmpty()) {
                start = "seq-null";
                end = "seq-null";
                size = 0;
            } else {
                start = String.valueOf(cacheList.get(0));
                end = cacheList.get(cacheList.size() - 1).toString();
                size = cacheList.size();
            }
            log.info("获取序列:{} 结束, seqSize={}, start:{}, endSeq:{}", key, size, start, end);
            return cacheList;
        } catch (Exception e) {
            log.error("转换传输序列出错！", e);
        }
        return null;
    }

    private int getGeneratedSeqSize(int sequenceNumber) {
        return (int) (sequenceNumber * RandomUtil.getNextDouble(0.5,1));
    }

    public Boolean sequenceCodeValidation(String sequenceCode, String tenantId) {
        if (StringUtils.isBlank(sequenceCode)) {
            return false;
        }
        //要根据状态获取
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        map.put("seqCode", sequenceCode);
        map.put("sequenceStatus", SequenceConstant.designStateOn);
        SeqDesignPo seqDesignPo = seqDesignService.getObjectByCode(map);
        if (Objects.isNull(seqDesignPo)) {
            return false;
        }

        String key = sequenceCode + SequenceConstant.TenantPostfix + seqDesignPo.getTenantId();
        AbstractSeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(key);

        if (seqFlowEngine == null) {
            try {
                seqFlowEngineFactory.doCreateEngine(seqDesignPo.getId());
                seqFlowEngine = SeqHolder.getEngineMap().get(key);
                seqFlowEngine.run();
            } catch (SequenceException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public BlockingQueue<Object> getSequenceQueue(String sequenceCode) {
        return null;
    }

    @Override
    public BigInteger getBigIntSequence(String sequenceCode) {
        //服务端暂时不一条条提供序列，仅做集合缓存后，在客户端提供
        return null;
    }

    @Override
    public String getStringSequence(String sequenceCode) {

        //服务端暂时不一条条提供序列，仅做集合缓存后，在客户端提供

        return null;
    }

    @Override
    public BigInteger getSerialBigIntSequence(String sequenceCode) {
        //服务端暂时不一条条提供序列，仅做集合缓存后，在客户端提供


        return null;
    }

    @Override
    public Boolean clientCacheQueueCallback(SeqCallbackDto SeqCallbackDto) {
        return null;
    }

    @Override
    public String getSerialStringSequence(String sequenceCode) {

        //服务端暂时不一条条提供序列，仅做集合缓存后，在客户端提供
        return null;
    }


    @Override
    public SeqObj getSeqDesignObj(String seqCode) {
        // TODO Auto-generated method stub
        return null;
    }
}
