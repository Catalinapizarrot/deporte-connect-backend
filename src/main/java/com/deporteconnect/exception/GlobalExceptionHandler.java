package com.deporteconnect.exception;

import com.deporteconnect.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador centralizado de excepciones.
 * Convierte las excepciones de la app en respuestas JSON consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // â”€â”€â”€ 404: recurso no encontrado â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
        ResourceNotFoundException ex, HttpServletRequest req
    ) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), req, null);
    }

    // â”€â”€â”€ 400: regla de negocio violada â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
        BusinessException ex, HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST, "OperaciÃ³n no permitida", ex.getMessage(), req, null);
    }

    // â”€â”€â”€ 400: datos invÃ¡lidos en el request â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest req
    ) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors()) {
            errors.put(e.getField(), e.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Datos invÃ¡lidos", "Revisa los errores de validaciÃ³n", req, errors);
    }

    // â”€â”€â”€ 401: credenciales incorrectas â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
        BadCredentialsException ex, HttpServletRequest req
    ) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", ex.getMessage(), req, null);
    }

    // â”€â”€â”€ 500: cualquier otro error â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
        Exception ex, HttpServletRequest req
    ) {
        ex.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
            "OcurriÃ³ un error inesperado", req, null);
    }

    private ResponseEntity<ApiErrorResponse> build(
        HttpStatus status, String error, String message,
        HttpServletRequest req, Map<String, String> validationErrors
    ) {
        ApiErrorResponse body = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(error)
            .message(message)
            .path(req.getRequestURI())
            .validationErrors(validationErrors)
            .build();
        return ResponseEntity.status(status).body(body);
    }
}
