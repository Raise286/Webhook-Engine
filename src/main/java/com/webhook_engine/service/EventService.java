package com.webhook_engine.service;

import com.webhook_engine.domain.WebhookEvent;
import com.webhook_engine.dto.DeliveryAttemptResponse;
import com.webhook_engine.dto.EventRequest;
import com.webhook_engine.dto.EventResponse;
import com.webhook_engine.repository.DeliveryAttemptRepository;
import com.webhook_engine.repository.SubscriptionRepository;
import com.webhook_engine.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service de publication et dispatch des événements webhook.
 * Quand un événement est publié, ce service trouve tous les abonnés
 * correspondants et déclenche la livraison via DeliveryService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final WebhookEventRepository webhookEventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final com.webhook_engine.service.DeliveryService deliveryService;





    /**
     * Publie un événement et le dispatche à tous les abonnés actifs.
     * La livraison est asynchrone (non-bloquante).
     */
    public Mono<EventResponse> publishEvent(EventRequest request) {
        log.info("Publication de l'événement : type={}, source={}",
                request.getEventType(), request.getSourceApp());

        WebhookEvent event = WebhookEvent.builder()
                .eventType(request.getEventType())
                .sourceApp(request.getSourceApp())
                .payload(request.getPayload())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return webhookEventRepository.save(event)
                .flatMap(savedEvent -> {
                    // Dispatch asynchrone vers tous les abonnés actifs
                    subscriptionRepository
                            .findByEventTypeAndIsActiveTrue(savedEvent.getEventType())
                            .flatMap(subscription ->
                                    deliveryService.deliver(savedEvent, subscription)
                                            .subscribeOn(Schedulers.boundedElastic())
                            )
                            .subscribe(
                                    null,
                                    err -> log.error("Erreur lors du dispatch : {}", err.getMessage())
                            );

                    // Mise à jour du statut en DELIVERED
                    savedEvent.setStatus("DELIVERED");
                    return webhookEventRepository.save(savedEvent);
                })
                .map(this::toResponse);
    }

    /**
     * Retourne l'historique de tous les événements.
     */
    public Flux<EventResponse> getAllEvents() {
        return webhookEventRepository.findAll()
                .map(this::toResponse);
    }

    /**
     * Retourne les événements filtrés par type.
     */
    public Flux<EventResponse> getByEventType(String eventType) {
        return webhookEventRepository.findByEventType(eventType)
                .map(this::toResponse);
    }

    /**
     * Retourne les événements filtrés par application source.
     */
    public Flux<EventResponse> getBySourceApp(String sourceApp) {
        return webhookEventRepository.findBySourceApp(sourceApp)
                .map(this::toResponse);
    }

    /**
     * Retourne l'historique des tentatives de livraison pour un événement.
     */
    public Flux<DeliveryAttemptResponse> getDeliveryHistory(UUID eventId) {
        return deliveryAttemptRepository.findByEventId(eventId)
                .map(attempt -> DeliveryAttemptResponse.builder()
                        .id(attempt.getId())
                        .eventId(attempt.getEventId())
                        .subscriptionId(attempt.getSubscriptionId())
                        .attemptNumber(attempt.getAttemptNumber())
                        .status(attempt.getStatus())
                        .httpStatus(attempt.getHttpStatus())
                        .responseBody(attempt.getResponseBody())
                        .errorMessage(attempt.getErrorMessage())
                        .attemptedAt(attempt.getAttemptedAt())
                        .build());
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────

    private EventResponse toResponse(WebhookEvent e) {
        return EventResponse.builder()
                .id(e.getId())
                .eventType(e.getEventType())
                .sourceApp(e.getSourceApp())
                .payload(e.getPayload())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }
}