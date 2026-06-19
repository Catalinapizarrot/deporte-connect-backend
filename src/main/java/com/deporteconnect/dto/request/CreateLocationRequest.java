package com.deporteconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLocationRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Size(max = 250)
    private String address;

    private String commune;
    private String city;
    private Double latitude;
    private Double longitude;
}
