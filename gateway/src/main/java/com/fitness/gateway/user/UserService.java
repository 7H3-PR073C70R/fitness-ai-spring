package com.fitness.gateway.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Validating user with userId: {}", userId);
        return userServiceWebClient.get()
                .uri("/api/v1/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.error("User not found with userId: {}", userId);
                        return Mono.error(new RuntimeException("User not found with userId: " + userId));
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        log.error("Bad request for userId: {}", userId);
                        return Mono.error(new RuntimeException("Bad request for userId: " + userId));
                    } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        log.error("Unauthorized access for userId: {}", userId);
                        return Mono.error(new RuntimeException("Unauthorized access for userId: " + userId));
                    } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                        log.error("Forbidden access for userId: {}", userId);
                        return Mono.error(new RuntimeException("Forbidden access for userId: " + userId));
                    } else {
                        log.error("Error validating user with userId: {}", userId, e);
                        return Mono.error(new RuntimeException("Error validating user with userId: " + userId, e));
                    }
                });
    }

    public Mono<UserResponse> registerUser(UserRequest userRequest) {
        log.info("Registering user with email: {}", userRequest.getEmail());
        return userServiceWebClient.post()
                .uri("/api/v1/users/register")
                .bodyValue(userRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        log.error("Bad request for user registration: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Bad request for user registration: " + e.getMessage()));
                    } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                        log.error("User already exists with email: {}", userRequest.getEmail());
                        return Mono.error(new RuntimeException("User already exists with email: " + userRequest.getEmail()));
                    } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        log.error("Internal server error while registering user: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Internal server error while registering user: " + e.getMessage()));
                    }

                    else {
                        log.error("Error registering user: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("Error registering user: " + e.getMessage(), e));
                    }
                });
    }
}
