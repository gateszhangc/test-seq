package com.dap.sequence.api.core;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.api.util.DateUtils;

import java.util.concurrent.atomic.AtomicReference;


public abstract class AbstractSeqFlowEngine implements SeqFlowEngine {

    private AtomicReference<SeqProducer> seqProducer;

    /**
     * 序列配置对象
     */
    private SeqObj seqObj;

    /**
     * 序列标识
     */
    private String seqCode;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 序列核心处理方法
     */
    @Override
    public void run(){

        loadConf();

        seqObj.setTenantId(tenantId);
        generateSeq(seqObj);

    }
    /**
     * 刷新引擎配置对象调用方法
     */
    @Override
    public void refresh() {
        loadConf();
    }

    private void generateSeq(SeqObj seqObj) {
        seqObj.setTenantId(tenantId);
        seqProducer.get().createSeq(seqObj , SequenceConstant.DAY_BASE);

        seqProducer.get().createSeq(seqObj , DateUtils.getCurrentDate());
    }

    /**
     * 对应唯一的引擎加载所需的序列配置
     * @return
     */
    public abstract void loadConf();


    public String getSeqCode() {
        return seqCode;
    }

    public void setSeqCode(String seqCode) {
        this.seqCode = seqCode;
    }

    public void setSeqProducer(AtomicReference<SeqProducer> seqProducer) {
        this.seqProducer = seqProducer;
    }

    public AtomicReference<SeqProducer> getSeqProducer() {
        return seqProducer;
    }

    public SeqObj getSeqObj() {
        return seqObj;
    }

    public void setSeqObj(SeqObj seqObj) {
        this.seqObj = seqObj;
    }


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
