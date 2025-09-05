package com.dap.paas.spring.common.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author wanqf
 * @version 1.0
 * @date 2024/7/22 21:40
 * */


@Configuration
public class EncryptionPropertyConfig {
    @Bean(name = "encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver() {
        return new EncryptablePropertyResolver() {
            private final static java.lang.String ENC_CHAR = "enc@";

            /**
             * 解密密码
             * @param value
             * @return
             * */
            @Override
            public java.lang.String resolvePropertyValue(java.lang.String value) {

                if (!StringUtils.isEmpty(value) && value.startsWith(ENC_CHAR)) {
                    return resolveDESValue(value.substring(4));
                }
                return value;
            }

            /**
             * @Description 解密算法
             * @param value
             * @Return String
             * */

            private java.lang.String resolveDESValue(java.lang.String value) {
                try {
                    return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return value;
                }
            }
        };
    }

}
