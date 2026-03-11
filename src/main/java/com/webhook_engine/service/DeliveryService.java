package com.webhook_engine.service;

import com.webhook_engine.domain.DeliveryAttempt;
import com.webhook_engine.domain.Subscription;
import com.webhook_engine.domain.WebhookEvent;
import com.webhook_engine.repository.DeliveryAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service responsable de la livraison HTTP des webhooks vers les abonnés.
 * Gère les tentatives et le retry automatique en cas d'échec.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WebClient webClient;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    @Value("${webhook.delivery.max-attempts:3}")
    private int maxAttempts;

    @Value("${webhook.delivery.retry-delay-seconds:30}")
    private long retryDelaySeconds;



    /**
     * Envoie le payload d'un événement vers l'URL d'un abonnement.
     * Retry automatique jusqu'à maxAttempts en cas d'échec.
     */
    public Mono<Void> deliver(WebhookEvent event, Subscription subscription) {
        log.info("Livraison de l'événement {} vers {}", event.getId(), subscription.getTargetUrl());

        return attemptDelivery(event, subscription, 1);
    }

    private Mono<Void> attemptDelivery(WebhookEvent event, Subscription subscription, int attemptNumber) {
        return webClient.post()
                .uri(subscription.getTargetUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(event.getPayload())
                .retrieve()
                .toEntity(String.class)
                .flatMap(response -> {
                    log.info("Livraison réussie (tentative {}) vers {} - HTTP {}",
                            attemptNumber, subscription.getTargetUrl(), response.getStatusCode().value());

                    return saveAttempt(DeliveryAttempt.builder()
                            .eventId(event.getId())
                            .subscriptionId(subscription.getId())
                            .attemptNumber(attemptNumber)
                            .status("SUCCESS")
                            .httpStatus(response.getStatusCode().value())
                            .responseBody(response.getBody())
                            .attemptedAt(LocalDateTime.now())
                            .build());
                })
                .onErrorResume(ex -> {
                    String errorMsg = ex.getMessage();
                    Integer httpStatus = null;

                    if (ex instanceof WebClientResponseException wcEx) {
                        httpStatus = wcEx.getStatusCode().value();
                        errorMsg = "HTTP " + httpStatus + " - " + wcEx.getResponseBodyAsString();
                    }

                    log.warn("Échec de livraison (tentative {}/{}) vers {} : {}",
                            attemptNumber, maxAttempts, subscription.getTargetUrl(), errorMsg);

                    String finalErrorMsg = errorMsg;
                    Integer finalHttpStatus = httpStatus;

                    // Si on n'a pas atteint le max de tentatives → retry
                    if (attemptNumber < maxAttempts) {
                        return saveAttempt(DeliveryAttempt.builder()
                                .eventId(event.getId())
                                .subscriptionId(subscription.getId())
                                .attemptNumber(attemptNumber)
                                .status("RETRYING")
                                .httpStatus(finalHttpStatus)
                                .errorMessage(finalErrorMsg)
                                .attemptedAt(LocalDateTime.now())
                                .build())
                                .then(Mono.delay(Duration.ofSeconds(retryDelaySeconds)))
                                .then(attemptDelivery(event, subscription, attemptNumber + 1));
                    }

                    // Dernière tentative échouée
                    return saveAttempt(DeliveryAttempt.builder()
                            .eventId(event.getId())
                            .subscriptionId(subscription.getId())
                            .attemptNumber(attemptNumber)
                            .status("FAILED")
                            .httpStatus(finalHttpStatus)
                            .errorMessage(finalErrorMsg)
                            .attemptedAt(LocalDateTime.now())
                            .build());
                })
                .then();
    }

    private Mono<Void> saveAttempt(DeliveryAttempt attempt) {
        return deliveryAttemptRepository.save(attempt)
                .doOnSuccess(saved -> log.debug("Tentative sauvegardée : {}", saved.getId()))
                .then();
    }
}