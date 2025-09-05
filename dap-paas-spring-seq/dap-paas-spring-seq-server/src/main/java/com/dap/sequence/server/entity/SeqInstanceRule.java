package com.dap.sequence.server.entity;

import com.base.api.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author: cao
 * Data: 2022/1/19 14:54
 * @Descricption:
 */
public class SeqInstanceRule extends BaseEntity {

    /**
     * 关联id（序列设计主表id）
     */
    private String seqDesignId;
    /**
     * 规则名称
     */
    private String instanceRuleName;
    /**
     * 规则类型
     */
    private String instanceRuleType;

    /**
     * 规则信息
     */
    private String instanceRuleInfo;
    /**
     * 排序号
     */
    private Integer sortNo;

    public String getSeqDesignId() {
        return seqDesignId;
    }

    public void setSeqDesignId(String seqDesignId) {
        this.seqDesignId = seqDesignId;
    }

    public String getInstanceRuleName() {
        return instanceRuleName;
    }

    public void setInstanceRuleName(String instanceRuleName) {
        this.instanceRuleName = instanceRuleName;
    }

    public String getInstanceRuleType() {
        return instanceRuleType;
    }

    public void setInstanceRuleType(String instanceRuleType) {
        this.instanceRuleType = instanceRuleType;
    }

    public String getInstanceRuleInfo() {
        return instanceRuleInfo;
    }

    public void setInstanceRuleInfo(String instanceRuleInfo) {
        this.instanceRuleInfo = instanceRuleInfo;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
