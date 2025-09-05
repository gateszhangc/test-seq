package com.dap.sequence.client.task;


import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.ResultResponse;
import com.dap.sequence.client.balancer.Server;
import com.dap.sequence.client.snow.SnowflakeGenerator;
import com.dap.sequence.client.utils.K8sPodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import com.alibaba.fastjson.JSON;
import com.dap.sequence.api.dto.SeqSdkMonitorDto;
import com.dap.sequence.client.balancer.RestTemplateBalancer;
import com.dap.sequence.client.properties.ConfigBeanValue;
import com.dap.sequence.client.utils.OSUtils;
import com.dap.sequence.client.utils.OtherUtil;
import org.apache.commons.lang.StringUtils;


/**
 * @className DynamicScheduleTaskSdk
 * @description 定时发送sdk的监控信息到服务端做监控使用
 * @date 2023/11/23 09:58:44
 * @version: V23.06
 */
@Configuration
@EnableScheduling
public class DynamicScheduleTaskSdk implements SchedulingConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(DynamicScheduleTaskSdk.class);
	
    @Autowired
    public ConfigBeanValue cb;

    @Autowired
    RestTemplateBalancer restTemplateBalancer;

    @Value("${server.port:}")
    private String serverPort;

    /**
     * 服务IP
     */
    private static final String HOST_IP = OSUtils.getHostIp();

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        //当application配置workId或者未开启定时任务时，不开启心跳
        if ("off".equals(StringUtils.trim(cb.getRegisterTime()))) {
            return;
        }
        taskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                this::executeTask,
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cb.getRegisterTime()).nextExecutionTime(triggerContext);
                }
        );
    }


    private void executeTask() {
        try {
            SeqSdkMonitorDto dto = new SeqSdkMonitorDto();
            //先取CLUSTER_NAME环境变量，再取配置 dap.sequence.seqClusterName
            if (K8sPodUtil.isIsK8sPod()) {
                dto.setInstanceName(K8sPodUtil.getPodInstanceNameName());
            } else {
                dto.setHostIp(HOST_IP);
                dto.setPort(serverPort);
                dto.setInstanceName(HOST_IP + ":" + serverPort);
            }
            dto.setWorkId(SnowflakeGenerator.getWorkId());
            dto.setSdkName(StringUtils.isNotBlank(cb.getClientName()) ? cb.getClientName() : cb.getApplicationName());
            String content = JSON.toJSONString(dto);
            //请求序列服务端-workId心跳服务
            logger.info("Starting send heart beat to sequence server");
            String rspBody = restTemplateBalancer.postRequest(content, OtherUtil.SNOWFLAKE_HEART_BEAT);
            //请求失败时，记录日志
            if (StringUtils.isEmpty(rspBody)) {
                logger.warn("Failed to send heart beat to sequence server, please check if the sequence server has been started.");
                //触发告警
                restTemplateBalancer.getServer();
            } else {
                ResultResponse response = JSON.parseObject(rspBody, ResultResponse.class);
                if (response == null) {
                    logger.warn("Failed to send heart beat to sequence server,  the error message: Unable to obtain response from the server");
                } else if (ExceptionEnum.SUCCESS.getResultCode().equals(response.getCode())) {
                    logger.info("Successfully sent heart beat to sequence server");
                } else {
                    logger.warn("Failed to send heart beat to sequence server, data:{}, the error message: {}, ", response.getData(), response.getMsg());
                }
            }

        } catch (Exception e) {
            logger.error("Failed to send heart beat to sequence server, throw exception:", e);
        }
    }
}
