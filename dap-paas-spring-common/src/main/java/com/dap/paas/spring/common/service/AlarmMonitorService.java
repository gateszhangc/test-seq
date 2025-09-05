package com.dap.paas.spring.common.service;

import com.dap.paas.spring.common.pojo.AlarmParamData;

public interface AlarmMonitorService  {

    String queryPublicKey() throws Exception;
    String loginMonitor(String username,String password) throws Exception;
    public void processAlarm(AlarmParamData alarmData);

}
