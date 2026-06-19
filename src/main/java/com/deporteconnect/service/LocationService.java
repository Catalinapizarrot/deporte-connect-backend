package com.deporteconnect.service;

import com.deporteconnect.dto.request.CreateLocationRequest;
import com.deporteconnect.dto.response.LocationResponse;
import com.deporteconnect.model.Location;
import com.deporteconnect.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public List<LocationResponse> getAll() {
        return locationRepository.findAll().stream()
            .map(LocationResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public LocationResponse create(CreateLocationRequest request) {
        Location location = Location.builder()
            .name(request.getName())
            .address(request.getAddress())
            .commune(request.getCommune())
            .city(request.getCity())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();

        return LocationResponse.from(locationRepository.save(location));
    }
}
