package com.dap.sequence.api.dto;

import java.util.List;

public class SeqCallbackDto {


    /**
     * 回收需要回收序列的编号
     */
    private String sequenceCode;

    /**
     *回收编号队列
     */
    private List callbackNumQueue;

    public List getCallbackNumQueue() {
        return callbackNumQueue;
    }

    public void setCallbackNumQueue(List callbackNumQueue) {
        this.callbackNumQueue = callbackNumQueue;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }
}
