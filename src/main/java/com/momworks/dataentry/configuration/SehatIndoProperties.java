package com.momworks.dataentry.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "sehat-indonesiaku")
public class SehatIndoProperties {
    private String deviceName;
    private String platformVersion;
    private String appPackage;
    private String appiumUrl;
    private String phoneNumber;
    private String pin;
}
