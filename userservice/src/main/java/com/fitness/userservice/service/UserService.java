package com.fitness.userservice.service;

import com.fitness.userservice.dto.UserRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return UserResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .id(user.getId())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserResponse registerUser(@Valid UserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())) {
            final User savedUsed = userRepository.findByEmail(userRequest.getEmail());
            return UserResponse.builder()
                    .id(savedUsed.getId())
                    .keycloakId(savedUsed.getKeycloakId())
                    .email(savedUsed.getEmail())
                    .firstName(savedUsed.getFirstName())
                    .lastName(savedUsed.getLastName())
                    .password(savedUsed.getPassword())
                    .createdAt(savedUsed.getCreatedAt())
                    .updatedAt(savedUsed.getUpdatedAt())
                    .build();
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPassword(userRequest.getPassword());
        user.setKeycloakId(userRequest.getKeycloakId());
        User savedUsed = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUsed.getId())
                .keycloakId(user.getKeycloakId())
                .email(savedUsed.getEmail())
                .firstName(savedUsed.getFirstName())
                .lastName(savedUsed.getLastName())
                .password(savedUsed.getPassword())
                .createdAt(savedUsed.getCreatedAt())
                .updatedAt(savedUsed.getUpdatedAt())
                .build();
    }

    public Boolean existByUserId(String userId) {
        log.info("Checking if user exists with userId: {}", userId);
        return  userRepository.existsByKeycloakId(userId);
    }
}
