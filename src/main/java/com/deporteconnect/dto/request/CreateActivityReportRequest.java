package com.deporteconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateActivityReportRequest {

    @NotBlank(message = "El motivo del reporte es obligatorio")
    @Size(max = 80, message = "El motivo no puede superar 80 caracteres")
    private String reason;

    @Size(max = 1000, message = "La descripcion no puede superar 1000 caracteres")
    private String description;
}
