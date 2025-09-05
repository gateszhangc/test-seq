package com.dap.sequence.api.dto;

//import com.dap.sequence.api.dto.base.BaseRuleInfo;

import com.dap.sequence.api.dto.base.BaseRuleInfo;

public class Rule {
    private String ruleId;
    private String ruleCode;
    private String tenantId;
    private String ruleName;
    private String ruleType;
    private BaseRuleInfo ruleData;//对象

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public BaseRuleInfo getRuleData() {
        return ruleData;
    }

    public void setRuleData(BaseRuleInfo ruleData) {
        this.ruleData = ruleData;
    }

}
