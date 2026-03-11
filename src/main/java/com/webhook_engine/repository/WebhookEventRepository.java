package com.webhook_engine.repository;

import com.webhook_engine.domain.WebhookEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Repository réactif pour la gestion des événements webhook.
 */
@Repository
public interface WebhookEventRepository extends ReactiveCrudRepository<WebhookEvent, UUID> {

    /** Récupère tous les événements d'un type donné */
    Flux<WebhookEvent> findByEventType(String eventType);

    /** Récupère tous les événements publiés par une application source */
    Flux<WebhookEvent> findBySourceApp(String sourceApp);

    /** Récupère les événements par statut (PENDING, DELIVERED, FAILED) */
    Flux<WebhookEvent> findByStatus(String status);
}