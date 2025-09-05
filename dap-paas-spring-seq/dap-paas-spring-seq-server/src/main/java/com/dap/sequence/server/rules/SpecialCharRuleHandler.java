package com.dap.sequence.server.rules;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.dto.SpecialCharRuleInfo;
import com.dap.sequence.api.dto.base.BaseRuleInfo;
import com.dap.sequence.api.obj.SeqObj;

import java.util.Date;

/**
 * @Author: cao
 * Data: 2022/2/14 14:31
 * @Descricption: 特殊字符
 */
public class SpecialCharRuleHandler implements BaseRuleHandler {

    @Override
    public Object generateRule(String message, Date day, SeqObj seqContext, BaseRuleInfo ruleInfo) {
        SpecialCharRuleInfo specialCharRuleInfo=(SpecialCharRuleInfo)ruleInfo;
        String placeholderInfo = "{" + specialCharRuleInfo.getPlaceholderRule() +
                "," + (specialCharRuleInfo.getPlaceholderRule() == 0 ? "" : specialCharRuleInfo.getPlaceholderLength() + "") + "}";
        return placeholderInfo;
    }

    @Override
    public String getRuleName() {
        return SequenceConstant.SPECIAL_RULE;
    }
}
