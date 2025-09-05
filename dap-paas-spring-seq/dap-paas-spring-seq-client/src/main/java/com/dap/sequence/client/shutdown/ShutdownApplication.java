package com.dap.sequence.client.shutdown;

import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.dto.SeqCallbackDto;
import com.dap.sequence.client.core.SequenceQueue;
import com.dap.sequence.client.core.SequenceQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className ShutdownApplication
 * @description 关机操作类
 * @date 2023/11/23 09:58:44
 * @version: V23.06
 */
@Component
public class ShutdownApplication implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownApplication.class);


    @Autowired
    private SeqConsumer seqConsumer;

    @Override
    public void afterPropertiesSet() {
        /**
         * 回收对应序列号吗
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    LOGGER.info("开始序列回收...");
                    ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
                    for (String seqCode : SEQUENCE_QUEUE_MAP.keySet()) {
                        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode);
                        List<Object> seqList = new ArrayList<>();

                        // 当前1号段未使用序列
                        if (sequenceQueue != null && sequenceQueue.getCacheBlockingQueue() != null && !sequenceQueue.getCacheBlockingQueue().isEmpty()) {
                            sequenceQueue.getCacheBlockingQueue().drainTo(seqList);
                        }

                        // 2号段未使用序列
                        Optional.ofNullable(sequenceQueue)
                                .map(SequenceQueue::getQuenList)
                                .filter(list -> !list.isEmpty())
                                .ifPresent(list -> list.forEach(que -> que.drainTo(seqList)));

                        if(!CollectionUtils.isEmpty(seqList)) {
                            SeqCallbackDto seqCallbackDto = new SeqCallbackDto();
                            seqCallbackDto.setSequenceCode(seqCode);
                            seqCallbackDto.setCallbackNumQueue(seqList);
                            seqConsumer.clientCacheQueueCallback(seqCallbackDto);
                            LOGGER.info("客户端序列回收开始，编号：{}，回收数量：{}，起始：{}，结束：{}" , seqCode , seqList.size() , seqList.get(0) , seqList.get(seqList.size()-1) );
                        }
                    }
                    LOGGER.info("序列回收结束...");
                } catch (Exception e) {
                    LOGGER.error("afterPropertiesSet error.msg = {}", e.getMessage(), e);
                }
            }
        });
    }
}
