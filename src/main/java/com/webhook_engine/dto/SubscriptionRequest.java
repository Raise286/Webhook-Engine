package com.webhook_engine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête pour créer un abonnement webhook.
 * Une application externe envoie ce DTO pour s'abonner à un type d'événement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotBlank(message = "Le nom de l'application est obligatoire")
    private String appName;

    @NotBlank(message = "L'URL cible est obligatoire")
    private String targetUrl;

    @NotBlank(message = "Le type d'événement est obligatoire")
    private String eventType;
}