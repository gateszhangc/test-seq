package com.dap.sequence.server.rules;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.dto.EnumRuleInfo;
import com.dap.sequence.api.dto.base.BaseRuleInfo;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.server.core.ThreadLocalVariables;

import java.util.Date;

public class EnumRuleHandler implements BaseRuleHandler {
    @Override
    public Object generateRule(String message, Date day, SeqObj seqContext, BaseRuleInfo ruleInfo) {
        EnumRuleInfo enumRuleInfo = (EnumRuleInfo) ruleInfo;

        int indexResult = Integer.parseInt(ThreadLocalVariables.get(enumRuleInfo).toString());
        int index = Integer.parseInt(ThreadLocalVariables.get(enumRuleInfo).toString());

        if ((index + enumRuleInfo.getNumberStep()) > (enumRuleInfo.getEnumStore().size())) {
            indexResult = (index + enumRuleInfo.getNumberStep() - enumRuleInfo.getEnumStore().size()) % enumRuleInfo.getEnumStore().size();
        } else {
            indexResult = indexResult + enumRuleInfo.getNumberStep();
        }
        ThreadLocalVariables.set(enumRuleInfo, indexResult);
        return enumRuleInfo.getEnumStore().get(indexResult - 1);
    }

    @Override
    public String getRuleName() {
        return SequenceConstant.ENUM_RUlE;
    }
}
