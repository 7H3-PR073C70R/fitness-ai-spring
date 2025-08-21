package com.fitness.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;


@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi activityApi() {
        return GroupedOpenApi.builder()
                .group("activity")
                .pathsToMatch("/activity/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info().title("Activity Service API")))
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info().title("User Service API")))
                .build();
    }

    @Bean
    public GroupedOpenApi aiApi() {
        return GroupedOpenApi.builder()
                .group("ai")
                .pathsToMatch("/ai/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info().title("AI Service API")))
                .build();
    }

}
