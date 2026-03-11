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
 * Représente un abonnement d'une application externe à un type d'événement.
 * Quand un événement de ce type est publié, le module envoie une requête HTTP
 * vers le targetUrl de l'abonné.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("subscriptions")
public class Subscription {

    @Id
    private UUID id;

    /** Nom de l'application abonnée (ex: "app-paiement", "crm-service") */
    @Column("app_name")
    private String appName;

    /** URL vers laquelle le webhook sera envoyé */
    @Column("target_url")
    private String targetUrl;

    /** Type d'événement écouté (ex: "payment.success", "order.created") */
    @Column("event_type")
    private String eventType;

    /** Permet de désactiver un abonnement sans le supprimer */
    @Column("is_active")
    private Boolean isActive;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}