package com.deporteconnect.config;

import com.deporteconnect.model.Location;
import com.deporteconnect.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationDataInitializer implements CommandLineRunner {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (locationRepository.count() > 0) {
            return;
        }

        locationRepository.saveAll(List.of(
                Location.builder()
                        .name("Parque Bustamante")
                        .address("Av. General Bustamante, Providencia")
                        .commune("Providencia")
                        .city("Santiago")
                        .latitude(-33.4372)
                        .longitude(-70.6337)
                        .build(),
                Location.builder()
                        .name("Parque Inés de Suárez")
                        .address("Antonio Varas 151, Providencia")
                        .commune("Providencia")
                        .city("Santiago")
                        .latitude(-33.4359)
                        .longitude(-70.5987)
                        .build(),
                Location.builder()
                        .name("Parque Balmaceda")
                        .address("Av. Providencia, Providencia")
                        .commune("Providencia")
                        .city("Santiago")
                        .latitude(-33.4320)
                        .longitude(-70.6261)
                        .build(),
                Location.builder()
                        .name("Parque Forestal")
                        .address("Ismael Valdés Vergara, Santiago Centro")
                        .commune("Santiago Centro")
                        .city("Santiago")
                        .latitude(-33.4354)
                        .longitude(-70.6420)
                        .build(),
                Location.builder()
                        .name("Parque O'Higgins")
                        .address("Av. Matta 1100, Santiago Centro")
                        .commune("Santiago Centro")
                        .city("Santiago")
                        .latitude(-33.4626)
                        .longitude(-70.6604)
                        .build(),
                Location.builder()
                        .name("Plaza Ñuñoa")
                        .address("Av. Irarrázaval 3550, Ñuñoa")
                        .commune("Ñuñoa")
                        .city("Santiago")
                        .latitude(-33.4548)
                        .longitude(-70.5994)
                        .build(),
                Location.builder()
                        .name("Parque Juan XXIII")
                        .address("Juan Moya Morales, Ñuñoa")
                        .commune("Ñuñoa")
                        .city("Santiago")
                        .latitude(-33.4540)
                        .longitude(-70.5866)
                        .build(),
                Location.builder()
                        .name("Parque Ramón Cruz")
                        .address("Av. Ramón Cruz, Ñuñoa")
                        .commune("Ñuñoa")
                        .city("Santiago")
                        .latitude(-33.4660)
                        .longitude(-70.5934)
                        .build(),
                Location.builder()
                        .name("Parque Estadio Nacional")
                        .address("Av. Grecia 2001, Ñuñoa")
                        .commune("Ñuñoa")
                        .city("Santiago")
                        .latitude(-33.4647)
                        .longitude(-70.6109)
                        .build()
        ));
    }
}
