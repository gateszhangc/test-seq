package com.dap.sequence.server.rules;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.dto.NumberRuleInfo;
import com.dap.sequence.api.dto.base.BaseRuleInfo;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.server.core.ThreadLocalVariables;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

public class NumberRuleHandler implements BaseRuleHandler {

    @Override
    public Object generateRule(String message, Date day, SeqObj seqContext, BaseRuleInfo ruleInfo) {
        NumberRuleInfo numberRuleInfo = (NumberRuleInfo) ruleInfo;
        long currentData = Long.parseLong(ThreadLocalVariables.get(numberRuleInfo).toString());

        // currentData为0，赋初值，不为0加步长
        if (currentData < numberRuleInfo.getNumberStart()) {
            currentData = numberRuleInfo.getNumberStart();
        } else {
            currentData += numberRuleInfo.getNumberStep();
        }
        currentData = getSeqNumberCurrentData(numberRuleInfo, currentData);
        ThreadLocalVariables.set(numberRuleInfo,currentData);

        if (StringUtils.isNotBlank(numberRuleInfo.getNumberFormat())) {
            DecimalFormat integerNumber = new DecimalFormat();
            integerNumber.applyPattern(numberRuleInfo.getNumberFormat());
            return integerNumber.format(currentData);
        }
        return currentData;

    }

    @Override
    public String getRuleName() {
        return SequenceConstant.NUMBER_RULE;
    }

    public static long getSeqNumberCurrentData(NumberRuleInfo numberRuleInfo, long currentData) {
        if (currentData > numberRuleInfo.getNumberEnd()) {
            if (Objects.equals(numberRuleInfo.getNumberCircle(), NumberUtils.INTEGER_ZERO)) {
                return currentData;
            } else if (Objects.equals(numberRuleInfo.getNumberCircle(), NumberUtils.INTEGER_ONE)) {
                return numberRuleInfo.getNumberStart();
            } else if (Objects.equals(numberRuleInfo.getNumberCircle(), 2)) {
                return numberRuleInfo.getNumberEnd();
            } else {
                return currentData;
            }
        } else {
            return currentData;
        }

    }
}
