package com.dap.sequence.client.snow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 监听ContextRefreshedEvent事件，完成SnowflakeGenerator第一次调用
 *
 * @author lyf
 * @date 2024/1/24
 */

@Component
public class SnowflakeInstanceListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeInstanceListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /*logger.info("Starting init SnowflakeGenerator.Snowflake");
        final String nextIdStr = SnowflakeGenerator.nextIdStr();
        logger.info("Ended init SnowflakeGenerator.Snowflake, first id is {}", nextIdStr);*/
    }
}
