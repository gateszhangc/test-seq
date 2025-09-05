package com.dap.sequence.api.core;


import com.dap.sequence.api.dto.SeqCallbackDto;
import com.dap.sequence.api.dto.SeqTransferDto;
import com.dap.sequence.api.obj.SeqObj;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * @Description: 序列消费者对象
 * @Author: liu
 * @Date: 2022/2/11
 * @Version: 1.0.0
 */
public interface SeqConsumer {


    BlockingQueue<Object> getSequenceQueue(String sequenceCode);
    /**
     * 无序序列
     * 返回BigInteger类型的序列值
     *
     * @param sequenceCode
     * @return
     */
    BigInteger getBigIntSequence( String sequenceCode);

    /**
     * 无序序列
     * 返回String类型的序列值
     * @param sequenceCode
     * @return
     */
      String  getStringSequence( String sequenceCode);

    /**
     * 有序序列
     * 返回BigInteger类型的序列值
     * @param sequenceCode
     * @return
     */
    BigInteger getSerialBigIntSequence( String sequenceCode);

    /**
     * 有序序列
     * 返回String类型的序列值
     * @param sequenceCode
     * @return
     */
    String  getSerialStringSequence( String sequenceCode);

    /**
     * 从服务端获取序列对象
     * @param
     * @return
     */
    SeqTransferDto getSeqFormServer(String seqCode ,String tenantId);


    /**
     * 序列回收
     * @param SeqCallbackDto
     * @return
     */
    Boolean clientCacheQueueCallback(SeqCallbackDto SeqCallbackDto);

    /**
     * 请求server端 获取 序列规则信息
     * @param seqCode
     * @return
     */
	SeqObj getSeqDesignObj(String seqCode);
}
