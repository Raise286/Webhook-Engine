package com.webhook_engine.repository;

import com.webhook_engine.domain.DeliveryAttempt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository réactif pour l'historique des tentatives de livraison.
 */
@Repository
public interface DeliveryAttemptRepository extends ReactiveCrudRepository<DeliveryAttempt, UUID> {

    /** Historique complet pour un événement */
    Flux<DeliveryAttempt> findByEventId(UUID eventId);

    /** Historique pour un abonnement donné */
    Flux<DeliveryAttempt> findBySubscriptionId(UUID subscriptionId);

    /** Dernière tentative pour un couple event + subscription */
    Mono<DeliveryAttempt> findTopByEventIdAndSubscriptionIdOrderByAttemptNumberDesc(
            UUID eventId, UUID subscriptionId);

    /** Compte le nombre de tentatives pour un couple event + subscription */
    Mono<Long> countByEventIdAndSubscriptionId(UUID eventId, UUID subscriptionId);
}