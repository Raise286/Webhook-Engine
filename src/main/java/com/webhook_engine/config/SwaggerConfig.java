package com.webhook.engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger / OpenAPI pour la documentation des endpoints.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI webhookEngineOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Webhook Engine API")
                        .description("""
                                Module de webhooks universel.
                                Permet à n'importe quelle application de :
                                - S'abonner à des types d'événements (subscribe)
                                - Se désabonner (unsubscribe)
                                - Publier des événements vers tous les abonnés
                                - Consulter l'historique des livraisons
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Webhook Engine")
                                .email("contact@webhook-engine.com")
                        )
                );
    }
}