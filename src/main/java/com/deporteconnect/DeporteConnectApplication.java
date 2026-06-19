package com.deporteconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Deporte Connect - API Backend
 *
 * Punto de entrada de la aplicaciÃ³n Spring Boot.
 *
 * Para ejecutar:
 *   mvnw spring-boot:run
 *
 * Endpoints disponibles:
 *   http://localhost:8080/swagger-ui.html   â† documentaciÃ³n
 *   http://localhost:8080/h2-console        â† base de datos (dev)
 */
@SpringBootApplication
public class DeporteConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeporteConnectApplication.class, args);
        System.out.println("""

            â•”â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•—$#$$
            â•‘   ðŸƒ DEPORTE CONNECT BACKEND INICIADO             â•‘
            â•‘                                                   â•‘
            â•‘   API:      http://localhost:8080                 â•‘
            â•‘   Swagger:  http://localhost:8080/swagger-ui.html â•‘
            â•‘   H2 BDD:   http://localhost:8080/h2-console      â•‘
            â•šâ•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
            """);
    }
}
