package com.dap.sequence.server.entity;

import com.base.api.entity.BaseEntity;
import com.dap.sequence.api.dto.Rule;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: liu
 * Data: 2022/1/19 10:05
 * @Descricption:
 */
public class SeqDesignPo extends BaseEntity {

    /**
     * 序列名称
     */
    private String sequenceName;
    /**
     *序列编号
     */
    private String sequenceCode;
    /**
     *应用id
     */
    private String sequenceApplicationId;
    /**
     *描述
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
     * 规则
     */
    private String sequenceRule;
    /**
     * 回拨模式  0 全局序列 1：按日回拨 （到下一日时序号中数字从头开始）  2：按月回拨 3：按年回拨
     */
    private String callbackMode;

    /**  appcode */
    private String appCode;

    /**  accesskey */
    private String accessKey;

    /**
     * 上线时间
     */
    private Date onlineTime;

    /**
     * 下线时间
     */
    private Date offlineTime;

    /**
     * 上下线告警标识  0 不告警 1 告警
     */
    private String onlineOrOfflineAlarm;

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

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getOnlineOrOfflineAlarm() {
        return onlineOrOfflineAlarm;
    }

    public void setOnlineOrOfflineAlarm(String onlineOrOfflineAlarm) {
        this.onlineOrOfflineAlarm = onlineOrOfflineAlarm;
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
