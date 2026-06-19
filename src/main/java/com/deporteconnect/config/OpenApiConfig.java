package com.deporteconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura Swagger/OpenAPI.
 * Accesible en http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deporteConnectOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Deporte Connect API")
                .description("API REST de la aplicaciÃ³n mÃ³vil Deporte Connect")
                .version("1.0.0")
                .contact(new Contact().name("Equipo Deporte Connect")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components().addSecuritySchemes(
                "Bearer Authentication",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Ingresa el token JWT obtenido en /auth/login")
            ));
    }
}
