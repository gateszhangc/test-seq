package com.dap.sequence.api.obj;


import com.dap.sequence.api.dto.Rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 序列配置对象
 * @Author: liu
 * @Date: 2022/2/11
 * @Version: 1.0.0
 */
public class SeqObj {

    private String id;
    /**
     * 序列名称
     */
    private String sequenceName;
    /**
     * 序列编号
     */
    private String sequenceCode;
    /**
     * 应用id
     */
    private String sequenceApplicationId;
    /**
     * 描述
     */
    private String sequenceDesc;
    /**
     * 应用名称
     */
    private String sequenceApplicationName;
    /**
     * 生成数量
     */
    private Integer sequenceNumber;
    /**
     * 状态  1未完成 2完成
     */
    private String sequenceStatus;

    /**
     * 是否为纯数字类型
     */
    private boolean isNum = false;

    /**
     * 规则
     */
    private String sequenceRule;

    private String tenantId;

    /**
     * 序列规则对象链表
     */
    private List<Rule> ruleInfos = new ArrayList<>();
    
    private Date seqDay;//序列日期

    /**
     * 回拨模式  0 全局序列 1：按日回拨 （到下一日时序号中数字从头开始）  2：按月回拨 3：按年回拨
     */
    private String callbackMode;

    /**
     * 回拨告警标识  0 不告警 1 告警
     */
    private String callbackAlarm;

    /**
     * 回拨告警阈值(百分百)
     */
    private String callbackAlarmThreshold;

	public String getCallbackMode() {
		return callbackMode;
	}

	public void setCallbackMode(String callbackMode) {
		this.callbackMode = callbackMode;
	}

	public Date getSeqDay() {
		return seqDay;
	}

	public void setSeqDay(Date seqDay) {
		this.seqDay = seqDay;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public String getSequenceApplicationId() {
        return sequenceApplicationId;
    }

    public void setSequenceApplicationId(String sequenceApplicationId) {
        this.sequenceApplicationId = sequenceApplicationId;
    }

    public String getSequenceDesc() {
        return sequenceDesc;
    }

    public void setSequenceDesc(String sequenceDesc) {
        this.sequenceDesc = sequenceDesc;
    }

    public String getSequenceApplicationName() {
        return sequenceApplicationName;
    }

    public void setSequenceApplicationName(String sequenceApplicationName) {
        this.sequenceApplicationName = sequenceApplicationName;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSequenceStatus() {
        return sequenceStatus;
    }

    public void setSequenceStatus(String sequenceStatus) {
        this.sequenceStatus = sequenceStatus;
    }

    public String getSequenceRule() {
        return sequenceRule;
    }

    public void setSequenceRule(String sequenceRule) {
        this.sequenceRule = sequenceRule;
    }

    public List<Rule> getRuleInfos() {
        return ruleInfos;
    }

    public void setRuleInfos(List<Rule> ruleInfos) {
        this.ruleInfos = ruleInfos;
    }

    public boolean isNum() {
        return isNum;
    }

    public void setNum(boolean num) {
        isNum = num;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCallbackAlarm() {
        return callbackAlarm;
    }

    public void setCallbackAlarm(String callbackAlarm) {
        this.callbackAlarm = callbackAlarm;
    }

    public String getCallbackAlarmThreshold() {
        return callbackAlarmThreshold;
    }

    public void setCallbackAlarmThreshold(String callbackAlarmThreshold) {
        this.callbackAlarmThreshold = callbackAlarmThreshold;
    }
}
