package com.dap.sequence.server.entity;

import com.base.api.entity.BaseEntity;

/**
 * @Author: cao
 * Data: 2022/2/7 11:14
 * @Descricption: 序列服务端生成情况
 */
public class SeqCurNum extends BaseEntity {

    /**
     * 关联id（序列设计主表id）
     */
    private String seqInstanceRuleId;
    /**
     * 日期
     */
    private String inDay;

    /**
     * 执行次数
     */
    private Integer seqLock;

    /**
     * 当前号段
     */
    private String curVal;

    /**
     * 序列名称
     */
    private String instanceRuleName;

    public String getSeqInstanceRuleId() {
        return seqInstanceRuleId;
    }

    public void setSeqInstanceRuleId(String seqInstanceRuleId) {
        this.seqInstanceRuleId = seqInstanceRuleId;
    }

    public String getInDay() {
        return inDay;
    }

    public void setInDay(String inDay) {
        this.inDay = inDay;
    }

    public Integer getSeqLock() {
        return seqLock;
    }

    public void setSeqLock(Integer seqLock) {
        this.seqLock = seqLock;
    }

    public String getCurVal() {
        return curVal;
    }

    public void setCurVal(String curVal) {
        this.curVal = curVal;
    }

    public String getInstanceRuleName() {
        return instanceRuleName;
    }

    public void setInstanceRuleName(String instanceRuleName) {
        this.instanceRuleName = instanceRuleName;
    }
}
