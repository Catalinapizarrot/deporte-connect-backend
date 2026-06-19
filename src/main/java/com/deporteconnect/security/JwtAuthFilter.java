package com.deporteconnect.security;

import com.deporteconnect.model.User;
import com.deporteconnect.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Filtro JWT â€” versiÃ³n final V11.
 *
 * CAMBIO CRÃTICO: usa shouldNotFilter() para EVITAR completamente
 * el filtro en rutas pÃºblicas. Antes el filtro intentaba procesar
 * /deportes (que es pÃºblica), tocaba la BDD, y si la BDD estaba lenta
 * o el token estaba mal devolvÃ­a 500.
 *
 * Ahora:
 *   - Rutas pÃºblicas (/deportes, /auth/*, /swagger) â†’ el filtro NO se ejecuta
 *   - Rutas privadas â†’ valida el JWT correctamente
 *   - Cualquier excepciÃ³n se atrapa y se devuelve 401 con JSON limpio
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Rutas que NUNCA pasan por este filtro (siempre pÃºblicas) */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/",
            "/deportes",
            "/swagger-ui",
            "/v3/api-docs",
            "/h2-console",
            "/error",
            "/swagger-resources",
            "/webjars"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            log.debug("Saltando JWT filter para ruta pÃºblica: {}", path);
        }
        return isPublic;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String path = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        // Sin header â†’ seguir (Spring decidirÃ¡ si la ruta requiere auth)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String email = jwtService.extractEmail(jwt);

            if (email == null) {
                writeError(response, 401, "Token invÃ¡lido (sin email)");
                return;
            }

            // Solo aquÃ­ tocamos la BDD
            Optional<User> userOpt;
            try {
                userOpt = userRepository.findByEmail(email);
            } catch (Exception dbError) {
                log.error("Error de BDD buscando usuario {}: {}", email, dbError.getMessage());
                writeError(response, 503, "Servicio temporalmente no disponible");
                return;
            }

            if (userOpt.isEmpty()) {
                log.warn("Token con email {} pero usuario no existe", email);
                writeError(response, 401, "Tu sesiÃ³n expirÃ³. Por favor inicia sesiÃ³n nuevamente.");
                return;
            }

            if (!jwtService.isTokenValid(jwt, email)) {
                writeError(response, 401, "Token invÃ¡lido o expirado");
                return;
            }

            User user = userOpt.get();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            writeError(response, 401, "Tu sesiÃ³n expirÃ³");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            writeError(response, 401, "Token con firma invÃ¡lida");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            writeError(response, 401, "Token mal formado");
        } catch (Exception e) {
            log.error("Error procesando JWT en {}: {}", path, e.getMessage(), e);
            writeError(response, 401, "Error de autenticaciÃ³n");
        }
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) return;
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", status == 401 ? "Unauthorized" : "Service Unavailable");
        body.put("message", message);

        objectMapper.writeValue(response.getWriter(), body);
    }
}

