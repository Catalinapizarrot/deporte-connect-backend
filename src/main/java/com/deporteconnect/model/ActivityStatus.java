package com.deporteconnect.model;

/**
 * Estado de una actividad.
 *   OPEN      â†’ abierta, acepta inscripciones
 *   FULL      â†’ llena (cupo agotado)
 *   CANCELLED â†’ cancelada por el organizador
 *   FINISHED  â†’ ya finalizÃ³ (fecha pasada)
 */
public enum ActivityStatus {
    OPEN,
    FULL,
    UNDER_REVIEW,
    CANCELLED,
    FINISHED
}
