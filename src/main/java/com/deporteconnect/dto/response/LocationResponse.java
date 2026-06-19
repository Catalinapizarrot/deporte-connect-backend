package com.deporteconnect.dto.response;

import com.deporteconnect.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String name;
    private String address;
    private String commune;
    private String city;
    private Double latitude;
    private Double longitude;

    public static LocationResponse from(Location location) {
        return LocationResponse.builder()
            .id(location.getId())
            .name(location.getName())
            .address(location.getAddress())
            .commune(location.getCommune())
            .city(location.getCity())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .build();
    }
}
