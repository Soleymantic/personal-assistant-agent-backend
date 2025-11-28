package com.nejat.projects.aiadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.nejat.projects")
@ConfigurationPropertiesScan("com.nejat.projects")
public class AiadminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiadminApplication.class, args);
    }
}
