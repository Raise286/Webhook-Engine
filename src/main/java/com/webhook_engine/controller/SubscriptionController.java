package com.webhook_engine.controller;

import com.webhook_engine.dto.SubscriptionRequest;
import com.webhook_engine.dto.SubscriptionResponse;
import com.webhook_engine.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Endpoints pour la gestion des abonnements webhook.
 * N'importe quelle application peut s'abonner ou se désabonner via ces routes.
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Gestion des abonnements aux événements webhook")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;



    /**
     * S'abonner à un type d'événement.
     * POST /api/subscriptions
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "S'abonner à un événement",
            description = "Enregistre une URL cible pour recevoir les webhooks d'un type d'événement donné")
    public Mono<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        return subscriptionService.subscribe(request);
    }

    /**
     * Se désabonner (désactiver un abonnement).
     * DELETE /api/subscriptions/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Se désabonner",
            description = "Désactive un abonnement existant sans le supprimer")
    public Mono<Void> unsubscribe(@PathVariable UUID id) {
        return subscriptionService.unsubscribe(id);
    }

    /**
     * Lister tous les abonnements actifs.
     * GET /api/subscriptions
     */
    @GetMapping
    @Operation(summary = "Lister tous les abonnements actifs")
    public Flux<SubscriptionResponse> getAllActive() {
        return subscriptionService.getAllActive();
    }

    /**
     * Filtrer les abonnements par type d'événement.
     * GET /api/subscriptions/event/{eventType}
     */
    @GetMapping("/event/{eventType}")
    @Operation(summary = "Filtrer par type d'événement",
            description = "Retourne tous les abonnés actifs pour un type d'événement donné")
    public Flux<SubscriptionResponse> getByEventType(@PathVariable String eventType) {
        return subscriptionService.getByEventType(eventType);
    }

    /**
     * Lister les abonnements d'une application.
     * GET /api/subscriptions/app/{appName}
     */
    @GetMapping("/app/{appName}")
    @Operation(summary = "Lister les abonnements d'une application")
    public Flux<SubscriptionResponse> getByAppName(@PathVariable String appName) {
        return subscriptionService.getByAppName(appName);
    }
}