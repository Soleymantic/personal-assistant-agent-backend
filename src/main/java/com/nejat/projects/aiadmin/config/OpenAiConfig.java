package com.nejat.projects.aiadmin.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAiConfig {

    @Bean
    @Qualifier("openAiWebClient")
    public WebClient openAiWebClient(OpenAiProperties openAiProperties, WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(openAiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                .build();
    }
}
