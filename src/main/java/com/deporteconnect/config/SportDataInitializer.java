package com.deporteconnect.config;

import com.deporteconnect.model.Sport;
import com.deporteconnect.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(10)
@RequiredArgsConstructor
public class SportDataInitializer implements CommandLineRunner {

    private final SportRepository sportRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (sportRepository.count() > 0) {
            return;
        }

        sportRepository.saveAll(List.of(
                Sport.builder()
                        .name("Fútbol")
                        .iconKey("ic_soccer")
                        .color("#22C55E")
                        .build(),
                Sport.builder()
                        .name("Pádel")
                        .iconKey("ic_padel")
                        .color("#7C3AED")
                        .build(),
                Sport.builder()
                        .name("Básquetbol")
                        .iconKey("ic_basketball")
                        .color("#2563EB")
                        .build(),
                Sport.builder()
                        .name("Tenis")
                        .iconKey("ic_tennis")
                        .color("#111111")
                        .build(),
                Sport.builder()
                        .name("Vóleibol")
                        .iconKey("ic_volleyball")
                        .color("#3B82F6")
                        .build(),
                Sport.builder()
                        .name("Running")
                        .iconKey("ic_flash")
                        .color("#F97316")
                        .build()
        ));
    }
}
