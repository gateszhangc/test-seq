package com.dap.sequence.client.properties;

/**
 * @className CurSeq
 * @description TODO
 * @author renle
 * @date 2024/04/25 16:05:49 
 * @version: V23.06
 */
public class CusSeq {

    private Object seq;

    private int queueSize;

    public Object getSeq() {
        return seq;
    }

    public void setSeq(Object seq) {
        this.seq = seq;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
}
