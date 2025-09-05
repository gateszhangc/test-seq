package com.dap.sequence.server.rules;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.dto.StringRuleInfo;
import com.dap.sequence.api.dto.base.BaseRuleInfo;
import com.dap.sequence.api.obj.SeqObj;

import java.util.Date;

public class StringRuleHandler implements BaseRuleHandler {
    @Override
    public Object generateRule(String message, Date day, SeqObj seqContext, BaseRuleInfo ruleInfo)  {
        StringRuleInfo stringRuleInfo=(StringRuleInfo)ruleInfo;
        return stringRuleInfo.getInitData();
    }

    @Override
    public String getRuleName() {
        return SequenceConstant.STRING_RULE;
    }
}
