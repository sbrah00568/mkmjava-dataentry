package com.momworks.dataentry.sehatindonesiaku;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "sehat-indonesiaku")
public class DataEntryPropertiesDto {
    private String appPackage;
    private String appiumUrl;
    private String phoneNumber;
    private String pin;
}
