package com.dap.sequence.server.utils;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;


public class SeqTypeAnalyser {

    private static final String NUM_REG = "^[1-9]\\d*$";

    public static boolean checkNum(String str){
        if (StringUtils.isBlank(str)) {
            return false;
        }
        Pattern r = Pattern.compile(NUM_REG);
        return r.matcher(str).matches();
    }

}
