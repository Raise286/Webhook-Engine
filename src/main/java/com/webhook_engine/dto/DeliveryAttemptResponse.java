package com.webhook_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Réponse pour consulter l'historique d'une tentative de livraison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAttemptResponse {

    private UUID id;
    private UUID eventId;
    private UUID subscriptionId;
    private Integer attemptNumber;
    private String status;
    private Integer httpStatus;
    private String responseBody;
    private String errorMessage;
    private LocalDateTime attemptedAt;
}