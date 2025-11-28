package com.nejat.projects.aiadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aiadmin.openai")
public class OpenAiProperties {

    private String apiKey;

    private String model;

    private String baseUrl;
}
