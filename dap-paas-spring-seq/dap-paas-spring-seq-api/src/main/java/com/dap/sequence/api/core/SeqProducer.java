package com.dap.sequence.api.core;


import com.dap.sequence.api.obj.SeqObj;

import java.util.List;

public interface SeqProducer {


    /**
     * 创建序号
     * @param obj
     * @param sequenceCode
     */
    void createSeq(SeqObj obj, String sequenceCode);

    /**
     * 创建序号
     * @param obj
     * @param sequenceCode
     */
    void createSeq(SeqObj obj, String sequenceCode,int serverCacheTimes,int serverCacheCreateTimes);

    /**
     * 加载回收序号
     * @param sequenceCode
     * @param callBackNumList
     */
    void loadSeq(String sequenceCode, List callBackNumList,String tenantId);


}
