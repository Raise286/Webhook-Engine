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
 * Historique de chaque tentative de livraison d'un événement vers un abonné.
 * Permet de tracker les succès, échecs et retries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("delivery_attempts")
public class DeliveryAttempt {

    @Id
    private UUID id;

    /** Référence à l'événement concerné */
    @Column("event_id")
    private UUID eventId;

    /** Référence à l'abonnement cible */
    @Column("subscription_id")
    private UUID subscriptionId;

    /** Numéro de la tentative (1, 2, 3...) */
    @Column("attempt_number")
    private Integer attemptNumber;

    /**
     * Statut de la tentative :
     * SUCCESS → HTTP 2xx reçu
     * FAILED  → erreur réseau ou HTTP non-2xx
     * RETRYING → en attente de retry
     */
    @Column("status")
    private String status;

    /** Code HTTP retourné par l'application cible */
    @Column("http_status")
    private Integer httpStatus;

    /** Corps de la réponse (pour le débogage) */
    @Column("response_body")
    private String responseBody;

    /** Message d'erreur en cas d'échec */
    @Column("error_message")
    private String errorMessage;

    @Column("attempted_at")
    private LocalDateTime attemptedAt;
}