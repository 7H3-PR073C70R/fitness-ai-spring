package com.fitness.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private  String id;
    private String keycloakId;
    private  String email;
    private  String firstName;
    private String lastName;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
