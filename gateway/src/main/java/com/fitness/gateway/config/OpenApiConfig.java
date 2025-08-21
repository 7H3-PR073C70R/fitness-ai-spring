package com.fitness.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fitness Gateway API")
                        .version("1.0")
                        .description("API documentation for Fitness Gateway"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(r -> r.path("/activity-service/**")
//                        .uri("lb://ACTIVITY-SERVICE"))
//                .route(r -> r.path("/user-service/**")
//                        .uri("lb://USER-SERVICE"))
//                .route(r -> r.path("/ai-service/**")
//                        .uri("lb://AI-SERVICE"))
//                .build();
//    }

    // This bean defines the routes for fetching API documentation from microservices
    @Bean
    public RouteLocator openApiRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/activity-service/v3/api-docs")
                        .uri("lb://ACTIVITY-SERVICE"))
                .route(r -> r.path("/api/v1/users/v3/api-docs")
                        .uri("lb://USER-SERVICE"))
                .route(r -> r.path("/ai-service/v3/api-docs")
                        .uri("lb://AI-SERVICE"))
                .build();
    }

}
