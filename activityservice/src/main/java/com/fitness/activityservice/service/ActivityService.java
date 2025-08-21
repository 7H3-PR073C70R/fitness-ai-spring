package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {
        // Validate that user exists
        boolean userExists = userValidationService.validateUser(activityRequest.getUserId());
        if (!userExists) {
            throw new RuntimeException("User not found with userId: " + activityRequest.getUserId());
        }
        // Convert ActivityRequest to Activity entity
        Activity activity = Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .duration(activityRequest.getDuration())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();

        // Save the activity to the database
        final Activity savedActivity =   activityRepository.save(activity);

        // Publish activity to RabbitMQ for AI Processing

        try{
            rabbitTemplate.convertAndSend(exchangeName, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ: " + e.getMessage(), e);
        }

        // Convert Activity entity to ActivityResponse
        return mapToResponse(savedActivity);
    }



    public List<ActivityResponse> getUserActivities(String userId) {
        // Fetch activities for the user from the database
        List<Activity> activities = activityRepository.findByUserId(userId);

        // Convert List<Activity> to List<ActivityResponse>
        return activities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ActivityResponse getActivityById(String activityId) {
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + activityId));

    }


    private  ActivityResponse mapToResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType())
                .duration(activity.getDuration())
                .caloriesBurned(activity.getCaloriesBurned())
                .startTime(activity.getStartTime())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .additionalMetrics(activity.getAdditionalMetrics())
                .build();
    }


}
