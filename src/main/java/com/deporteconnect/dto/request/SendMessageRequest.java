package com.deporteconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank(message = "El mensaje no puede estar vacÃ­o")
    @Size(max = 1000, message = "El mensaje es demasiado largo")
    private String content;
}
