package com.webhook_engine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête pour publier un événement dans le moteur de webhooks.
 * Une application source envoie ce DTO pour notifier tous les abonnés.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Le type d'événement est obligatoire")
    private String eventType;

    @NotBlank(message = "Le nom de l'application source est obligatoire")
    private String sourceApp;

    @NotBlank(message = "Le payload est obligatoire")
    private String payload;
}