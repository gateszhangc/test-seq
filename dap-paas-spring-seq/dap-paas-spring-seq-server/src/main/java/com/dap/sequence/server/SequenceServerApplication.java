package com.dap.sequence.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zpj
 */
@SpringBootApplication(scanBasePackages = {"com.dap"})
@EnableScheduling
@EnableAsync
public class SequenceServerApplication {
    public static void main(String[] args) {

        SpringApplication.run(SequenceServerApplication.class, args);
    }


}
