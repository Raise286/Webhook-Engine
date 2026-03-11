package com.webhook_engine.service;

import com.webhook_engine.domain.Subscription;
import com.webhook_engine.dto.SubscriptionRequest;
import com.webhook_engine.dto.SubscriptionResponse;
import com.webhook_engine.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service de gestion des abonnements webhook.
 * Gère subscribe, unsubscribe et la consultation des abonnements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;



    /**
     * Crée un nouvel abonnement.
     * Si un abonnement identique existe déjà, le réactive s'il était inactif.
     */
    public Mono<SubscriptionResponse> subscribe(SubscriptionRequest request) {
        log.info("Nouvelle demande d'abonnement : {} -> {} pour l'événement {}",
                request.getAppName(), request.getTargetUrl(), request.getEventType());

        return subscriptionRepository
                .findByAppNameAndEventTypeAndTargetUrl(
                        request.getAppName(), request.getEventType(), request.getTargetUrl())
                .flatMap(existing -> {
                    // Réactivation si déjà existant mais inactif
                    existing.setIsActive(true);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return subscriptionRepository.save(existing);
                })
                .switchIfEmpty(
                        // Création si nouvel abonnement
                        subscriptionRepository.save(Subscription.builder()
                                .appName(request.getAppName())
                                .targetUrl(request.getTargetUrl())
                                .eventType(request.getEventType())
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build())
                )
                .map(this::toResponse);
    }

    /**
     * Désactive un abonnement (soft delete).
     */
    public Mono<Void> unsubscribe(UUID subscriptionId) {
        log.info("Désabonnement demandé pour l'id : {}", subscriptionId);

        return subscriptionRepository.findById(subscriptionId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Abonnement non trouvé : " + subscriptionId)))
                .flatMap(subscription -> {
                    subscription.setIsActive(false);
                    subscription.setUpdatedAt(LocalDateTime.now());
                    return subscriptionRepository.save(subscription);
                })
                .then();
    }

    /**
     * Retourne tous les abonnements actifs.
     */
    public Flux<SubscriptionResponse> getAllActive() {
        return subscriptionRepository.findByIsActiveTrue()
                .map(this::toResponse);
    }

    /**
     * Retourne tous les abonnements actifs pour un type d'événement.
     */
    public Flux<SubscriptionResponse> getByEventType(String eventType) {
        return subscriptionRepository.findByEventTypeAndIsActiveTrue(eventType)
                .map(this::toResponse);
    }

    /**
     * Retourne tous les abonnements d'une application.
     */
    public Flux<SubscriptionResponse> getByAppName(String appName) {
        return subscriptionRepository.findByAppName(appName)
                .map(this::toResponse);
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────

    private SubscriptionResponse toResponse(Subscription s) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .appName(s.getAppName())
                .targetUrl(s.getTargetUrl())
                .eventType(s.getEventType())
                .isActive(s.getIsActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}