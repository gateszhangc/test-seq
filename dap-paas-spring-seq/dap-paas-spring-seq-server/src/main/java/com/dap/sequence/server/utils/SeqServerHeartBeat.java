package com.dap.sequence.server.utils;

import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.server.entity.SeqServiceRegister;
import com.dap.sequence.server.service.SeqServiceRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SeqServerHeartBeat
 * @author scalor
 * @date 2022/03/03
 **/
@Component
public class SeqServerHeartBeat implements CommandLineRunner {
    private static final Logger log= LoggerFactory.getLogger(SeqServerHeartBeat.class);

    @Value("${server.port}")
    private Integer port;
    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private SeqServiceRegisterService seqServiceRegister;
    @Override
    public void run(String... args) throws Exception {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        log.debug("SeqServerHeartBeat start Timer for seq server node status ");
        SeqServiceRegister finalRegister = getSeqServiceRegister();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    seqServiceRegister.updateObject(finalRegister);
                } catch (Exception e) {
                    log.debug("SeqServerHeartBeat start Timer for seq server node status error ");
                }
            }
        }, 60, 10, TimeUnit.SECONDS);
    }

    /**
     *
     * @return
     * @throws SequenceException
     */
    private SeqServiceRegister getSeqServiceRegister() throws SequenceException {
        String hostIp = EnvUtils.getAddress();
        SeqServiceRegister register;
        HashMap<String, Object> map = new HashMap<>();
        map.put("hostIp",hostIp);
        map.put("applicationName",applicationName);
        map.put("port",port);
        register = seqServiceRegister.getObjectByMap(map);
        if(null==register){
            register=new SeqServiceRegister();
            register.setHostIp(hostIp);
            register.setPort(port);
            register.setApplicationName(applicationName);
            seqServiceRegister.saveObject(register);
        }else{
            seqServiceRegister.updateObject(register);
        }
        return register;
    }
}
