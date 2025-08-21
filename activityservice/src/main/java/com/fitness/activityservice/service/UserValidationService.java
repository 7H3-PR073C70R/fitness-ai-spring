package com.fitness.activityservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@AllArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        log.info("Validating user with userId: {}", userId);
        try {
            // Call the user service to validate the user
            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/v1/users//{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (WebClientResponseException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RuntimeException("User not found with userId: " + userId);
            else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new RuntimeException("Bad request for userId: " + userId);
            else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                throw new RuntimeException("Unauthorized access for userId: " + userId);
            else if (e.getStatusCode() == HttpStatus.FORBIDDEN)
                throw new RuntimeException("Forbidden access for userId: " + userId);
            else
                throw new RuntimeException("Error validating user with userId: " + userId, e);
        }
    }
}
