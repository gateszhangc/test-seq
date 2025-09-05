package com.dap.sequence.api.dto;


import com.dap.sequence.api.dto.base.BaseRuleInfo;

public class StringRuleInfo extends BaseRuleInfo {
    private  String initData;

    public StringRuleInfo(){}
    public StringRuleInfo(String initData ){

            this.initData = initData;
    }

    public String getInitData() {
        return initData;
    }

    public void setInitData(String initData) {
        this.initData = initData;
    }
}
