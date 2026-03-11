package com.webhook_engine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente un événement publié par une application source.
 * Le module se charge de livrer cet événement à tous les abonnés correspondants.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("webhook_events")
public class WebhookEvent {

    @Id
    private UUID id;

    /** Type de l'événement (ex: "payment.success", "user.created") */
    @Column("event_type")
    private String eventType;

    /** Nom de l'application qui publie l'événement */
    @Column("source_app")
    private String sourceApp;

    /** Données JSON de l'événement à transmettre aux abonnés */
    @Column("payload")
    private String payload;

    /**
     * Statut global de l'événement :
     * PENDING   → en attente de livraison
     * DELIVERED → livré à tous les abonnés
     * FAILED    → échec après tous les retries
     */
    @Column("status")
    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;
}