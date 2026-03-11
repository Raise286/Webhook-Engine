package com.webhook_engine.controller;

import com.webhook_engine.dto.DeliveryAttemptResponse;
import com.webhook_engine.dto.EventRequest;
import com.webhook_engine.dto.EventResponse;
import com.webhook_engine.service.EventService;
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
 * Endpoints pour la publication d'événements et la consultation de l'historique.
 * N'importe quelle application peut publier un événement via ces routes.
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Publication d'événements et historique des livraisons webhook")
public class EventController {

    private final EventService eventService;


    /**
     * Publier un événement → déclenche les webhooks vers tous les abonnés.
     * POST /api/events
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Publier un événement",
            description = "Publie un événement et le dispatche immédiatement à tous les abonnés actifs correspondants")
    public Mono<EventResponse> publish(@Valid @RequestBody EventRequest request) {
        return eventService.publishEvent(request);
    }

    /**
     * Lister tous les événements publiés.
     * GET /api/events
     */
    @GetMapping
    @Operation(summary = "Lister tous les événements publiés")
    public Flux<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Filtrer les événements par type.
     * GET /api/events/type/{eventType}
     */
    @GetMapping("/type/{eventType}")
    @Operation(summary = "Filtrer par type d'événement")
    public Flux<EventResponse> getByEventType(@PathVariable String eventType) {
        return eventService.getByEventType(eventType);
    }

    /**
     * Filtrer les événements par application source.
     * GET /api/events/source/{sourceApp}
     */
    @GetMapping("/source/{sourceApp}")
    @Operation(summary = "Filtrer par application source")
    public Flux<EventResponse> getBySourceApp(@PathVariable String sourceApp) {
        return eventService.getBySourceApp(sourceApp);
    }

    /**
     * Consulter l'historique des tentatives de livraison d'un événement.
     * GET /api/events/{eventId}/history
     */
    @GetMapping("/{eventId}/history")
    @Operation(summary = "Historique des livraisons",
            description = "Retourne toutes les tentatives de livraison pour un événement donné (succès, échecs, retries)")
    public Flux<DeliveryAttemptResponse> getDeliveryHistory(@PathVariable UUID eventId) {
        return eventService.getDeliveryHistory(eventId);
    }
}