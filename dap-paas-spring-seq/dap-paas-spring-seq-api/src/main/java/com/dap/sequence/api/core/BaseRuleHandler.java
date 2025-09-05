package com.dap.sequence.api.core;


import com.dap.sequence.api.dto.base.BaseRuleInfo;
import com.dap.sequence.api.obj.SeqObj;

import java.util.Date;

public interface BaseRuleHandler {

    Object generateRule(String message, Date day, SeqObj seqContext, BaseRuleInfo ruleInfo) ;

    String getRuleName();
}
