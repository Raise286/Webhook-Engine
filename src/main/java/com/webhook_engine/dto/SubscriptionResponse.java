package com.webhook_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Réponse retournée après création ou consultation d'un abonnement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private UUID id;
    private String appName;
    private String targetUrl;
    private String eventType;
    private Boolean isActive;
    private LocalDateTime createdAt;
}