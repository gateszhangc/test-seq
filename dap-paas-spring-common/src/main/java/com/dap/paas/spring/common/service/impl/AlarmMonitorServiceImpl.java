package com.dap.paas.spring.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dap.paas.spring.common.pojo.AlarmParamData;
import com.dap.paas.spring.common.service.AlarmMonitorService;
import com.dap.paas.spring.common.util.DateUtils;
import com.dap.paas.spring.common.util.RsaEncryptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@Service
public class AlarmMonitorServiceImpl implements AlarmMonitorService {

    @Resource
    private RestTemplate restTemplate;
    @Value("${monitor.queryPublicKey}")
    private String queryPublicKey;
    @Value("${monitor.loginMonitor}")
    private String loginMonitor;
    @Value("${monitor.pushAlarm}")
    private String pushAlarm;
    @Value("${monitor.user}")
    private String username;
    @Value("${monitor.pwd}")
    private String password;

    private static final Logger log= LoggerFactory.getLogger(AlarmMonitorServiceImpl.class);

    @Override
    public String queryPublicKey() {
        try {
            ResponseEntity responseEntity = restTemplate.getForEntity(queryPublicKey, String.class);
            log.info("queryPublicKey response return {} ", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JSONObject resObject = JSON.parseObject(responseEntity.getBody().toString());
                //true表示成功，false表示失败
                String ret = resObject.get("ret").toString();
                if("true".equals(ret)) {
                    return resObject.get("data").toString();

                } else {
                    String error = String.valueOf(resObject.get("error"));
                    log.error("调用监控平台服务获取公钥失败：" + error);
                }
            }
        } catch (Exception e){
            log.error("调用监控平台服务获取公钥失败",e);
            return null;
        }
        return null;
    }

    @Override
    public String loginMonitor(String username,String password)  {
        try {
            log.info("loginMonitor request username {} password {} ", username,password);
            String publickkey = queryPublicKey();
            String pwd = RsaEncryptionUtil.getRsaEncryptionPassword(publickkey,password);

            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("username",username);
            paramMap.put("password",pwd);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(loginMonitor, paramMap, String.class);
            log.info("loginMonitor response return {} ", responseEntity);
            if (responseEntity.getStatusCode() == HttpStatus.OK) { // http请求成功
                JSONObject jsonObject = JSON.parseObject(responseEntity.getBody());
                //true表示成功，false表示失败
                String ret = jsonObject.get("ret").toString();
                if("true".equals(ret)) {
                    JSONObject data = JSON.parseObject(jsonObject.get("data").toString());
                    JSONObject userInfo = JSON.parseObject(data.get("userinfo").toString());
                    return userInfo.get("token").toString();
                } else {
                    String error = String.valueOf(jsonObject.get("error"));
                    log.error("调用监控平台服务获取公钥失败：" + error);
                }
            }
            return null;
        } catch (Exception e){
            log.error("调用监控平台服务获取token失败",e);
            return null;
        }
    }

    /**
     * 调用监控平台告警
     * @param alarmData
     */
    public void processAlarm(AlarmParamData alarmData) {
        String token = loginMonitor(username,password);
        alarmData.setToken(token);
        alarmData.setTime(DateUtils.getTimeSpan());
        List<AlarmParamData>  alarmParamData = new ArrayList<>();
        alarmParamData.add(alarmData);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token",alarmData.getToken());
            String paramData = JSON.toJSONString(alarmParamData);
            log.info("调用监控告警平台参数: {}",paramData);
            HttpEntity<String> entity = new HttpEntity<>(paramData, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(pushAlarm, entity, String.class);
            String result = responseEntity.getBody();
            if(StringUtils.isNotBlank(result)){
                JSONObject jso = JSONObject.parseObject(result);
                Integer status = (Integer)jso.get("status");
                if(status.intValue() != 200){
                    log.error("调用监控告警平台返回失败！！");
                }
            } else {
                log.error("调用监控告警平台失败！！");
            }
        } catch (Exception e) {
            log.error("调用监控告警平台失败！！");
        }
    }

}
