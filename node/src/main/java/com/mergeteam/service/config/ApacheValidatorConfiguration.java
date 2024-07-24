package com.mergeteam.service.config;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheValidatorConfiguration {

    @Bean
    public EmailValidator createEmailValidator() {
        return EmailValidator.getInstance();
    }
}
