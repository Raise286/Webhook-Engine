package com.webhook_engine.repository;

import com.webhook_engine.domain.Subscription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository réactif pour la gestion des abonnements.
 */
@Repository
public interface SubscriptionRepository extends ReactiveCrudRepository<Subscription, UUID> {

    /** Trouve tous les abonnements actifs pour un type d'événement donné */
    Flux<Subscription> findByEventTypeAndIsActiveTrue(String eventType);

    /** Trouve tous les abonnements d'une application */
    Flux<Subscription> findByAppName(String appName);

    /** Trouve tous les abonnements actifs */
    Flux<Subscription> findByIsActiveTrue();

    /** Vérifie si un abonnement existe déjà pour cette app + eventType + url */
    Mono<Subscription> findByAppNameAndEventTypeAndTargetUrl(
            String appName, String eventType, String targetUrl);
}