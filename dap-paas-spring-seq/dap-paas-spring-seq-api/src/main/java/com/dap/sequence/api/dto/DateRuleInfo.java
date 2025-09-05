package com.dap.sequence.api.dto;


import com.dap.sequence.api.dto.base.BaseRuleInfo;

public class DateRuleInfo extends BaseRuleInfo {

    private String dateFormat;

    public DateRuleInfo(){}
    public DateRuleInfo(String dateFormat){

        this.dateFormat = dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
