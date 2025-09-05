package com.dap.sequence.client.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.client.balancer.RestTemplateBalancer;
import com.dap.sequence.client.core.SequenceQueueFactory;
import com.dap.sequence.client.properties.ConfigBeanValue;
import com.dap.sequence.client.utils.OtherUtil;


/***
 * 定时获取集群下所有的实例信息
 */
@Configuration
@EnableScheduling
public class DynamicScheduleTaskUrl implements SchedulingConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicScheduleTaskUrl.class);
	

    @Autowired
    public ConfigBeanValue cb;
    @Autowired
    RestTemplateBalancer restTemplateBalancer;
    

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	if("off".equals(StringUtils.trimAllWhitespace(cb.getRefreshTime()))) {
    		return;
    	}
        taskRegistrar.addTriggerTask(
            //1.添加任务内容(Runnable)
            () -> executeTask(),
            //2.设置执行周期(Trigger)
            triggerContext -> {
                //2.3 返回执行周期(Date)
                return new CronTrigger(cb.getRefreshTime()).nextExecutionTime(triggerContext);
            }
        );
    }

    private void executeTask(){
    	updateServerUrl();
    }
    
    private void updateServerUrl() {
    	String body = restTemplateBalancer.postRequest(cb.getSeqClusterName() , OtherUtil.GET_URL);
    	if(!StringUtils.hasText(body)) {
    		logger.warn("request seqserver updateServerUrl fail,result null");
    		return ;
    	}
        JSONObject jsonObject = JSONObject.parseObject(body);
        
        String data = jsonObject.getString("data");
        if(jsonObject.getIntValue("code") == 200 && StringUtils.hasText(data) && data.contains(":")) {
        	OtherUtil.DEFULT_URLS = jsonObject.getString("data");
        }else {
        	logger.error("request seqserver fail , clusterName:{} , result:{}" , cb.getSeqClusterName() , jsonObject.toJSONString());
        }
    }

    
}
