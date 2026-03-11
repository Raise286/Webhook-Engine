package com.webhook_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Réponse retournée après publication d'un événement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private UUID id;
    private String eventType;
    private String sourceApp;
    private String payload;
    private String status;
    private LocalDateTime createdAt;
}