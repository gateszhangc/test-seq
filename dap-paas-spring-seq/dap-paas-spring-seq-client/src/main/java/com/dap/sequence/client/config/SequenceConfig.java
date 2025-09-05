package com.dap.sequence.client.config;

import java.net.Inet4Address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.dap.sequence.client.core.SequenceQueueFactory;
import com.dap.sequence.client.properties.ConfigBeanValue;
import com.dap.sequence.client.snow.Snowflake;
import com.dap.sequence.client.snow.SnowflakeFile;
import com.dap.sequence.client.utils.OSUtils;

@ComponentScan("com.dap.sequence.client")
@Configuration
public class SequenceConfig {

    @Value("${dap.sequence.sequenceQueueCapacity:100}")
    private int sequenceQueueCapacity;
    
  //缓存号段数量 ，默认双号段 压测时可适当调大一点
    @Value("${dap.sequence.bufferCount:2}")
    private int bufferCount;

    /**
     * 取缓存序号队列超时时间
     */
    @Value("${dap.sequence.timeout:2000}")
    private int timeout;


    @Value("${dap.sequence.useSequenceCache:true}")
    private boolean useSequenceCache;

    /**
     * 缓存队列清理范围数量
     */
    @Value("${dap.sequence.retainNum:30}")
    private int retainNum;

    /**
     * 客户端缓存补仓阈值，默认30%时补仓
     */
    @Value("${dap.sequence.clientCacheThreadHold:30}")
    private int clientCacheThreadHold;

    @Autowired
    ConfigBeanValue configBeanValue;

    @Bean
    public SequenceQueueFactory getSequenceQueueFactory(){
        SequenceQueueFactory sequenceQueueFactory= new SequenceQueueFactory();

        sequenceQueueFactory.setTimeout(this.timeout);
        sequenceQueueFactory.setSequenceQueueCapacity(this.sequenceQueueCapacity);
        sequenceQueueFactory.setUseSequenceCache(this.useSequenceCache);
        sequenceQueueFactory.setBufferCount(this.bufferCount);
        sequenceQueueFactory.setRetainNum(this.retainNum);
        sequenceQueueFactory.setClientCacheThreadHold(this.clientCacheThreadHold);
        return sequenceQueueFactory;
    }

    @Bean
    public Snowflake snowflake() {
    	Integer workerId = configBeanValue.getWorkerId();
    	if(workerId == null) {
			workerId = OSUtils.getHostId(OSUtils.getHostIp() + configBeanValue.getServerPort() ,63);
		}
		
		long dataCenterId = (SnowflakeFile.readerClock() + 1) & Snowflake.maxDataCenterId;
		
		SnowflakeFile.writerClock(dataCenterId);
		
		return new Snowflake(workerId , dataCenterId);
    }
//    @Bean
//    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
//        return new RestTemplate(factory);
//    }

//    @Bean
//    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(10000);//单位为ms
//        factory.setConnectTimeout(10000);//单位为ms
//        return factory;
//
//    }

}
