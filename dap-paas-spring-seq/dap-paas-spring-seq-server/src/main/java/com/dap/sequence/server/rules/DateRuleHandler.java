package com.dap.sequence.server.rules;



import java.text.SimpleDateFormat;
import java.util.Date;

import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.dto.DateRuleInfo;
import com.dap.sequence.api.dto.base.BaseRuleInfo;
import com.dap.sequence.api.obj.SeqObj;

public class DateRuleHandler implements BaseRuleHandler {


    @Override
    public Object generateRule(String message, Date day, SeqObj seqContext, BaseRuleInfo ruleInfo)  {
        DateRuleInfo dateRuleInfo=(DateRuleInfo)ruleInfo;
        SimpleDateFormat sdf = new SimpleDateFormat(dateRuleInfo.getDateFormat());
        return sdf.format(day == null ? new Date() : day);
    }

    @Override
    public String getRuleName() {
        return SequenceConstant.DATE_RUlE;
    }
}
