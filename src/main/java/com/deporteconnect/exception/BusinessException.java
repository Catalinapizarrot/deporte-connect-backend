package com.deporteconnect.exception;

/**
 * Errores de reglas de negocio: actividad llena, usuario ya inscrito, etc.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
