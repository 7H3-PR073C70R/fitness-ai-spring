package com.fitness.aiservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "recommendations")
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private String id;
    private String activityId;
    private String userId;
    private ActivityType activityType;
    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @ElementCollection
    @CollectionTable(name = "recommendation_improvements", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "improvements", columnDefinition = "TEXT")
    private List<String> improvements;

    @ElementCollection
    @CollectionTable(name = "recommendation_suggestions", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "suggestion", columnDefinition = "TEXT")
    private List<String> suggestions;

    @ElementCollection
    @CollectionTable(name = "recommendation_safety", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "safety", columnDefinition = "TEXT")
    private List<String> safety;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
