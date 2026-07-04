package com.deporteconnect.config;

import com.deporteconnect.model.Gender;
import com.deporteconnect.model.SkillLevel;
import com.deporteconnect.model.Sport;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.SportRepository;
import com.deporteconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(20)
@RequiredArgsConstructor
public class DemoUserDataInitializer implements CommandLineRunner {

    private static final String DEMO_EMAIL = "participante.demo@deporteconnect.cl";
    private static final String DEMO_PASSWORD = "Demo1234";

    private final UserRepository userRepository;
    private final SportRepository sportRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail(DEMO_EMAIL)) {
            return;
        }

        Set<Sport> interests = new HashSet<>();
        sportRepository.findByNameIgnoreCase("Fútbol")
                .or(() -> sportRepository.findAll().stream().findFirst())
                .ifPresent(interests::add);

        User user = User.builder()
                .email(DEMO_EMAIL)
                .passwordHash(passwordEncoder.encode(DEMO_PASSWORD))
                .fullName("Participante Demo")
                .phone("912345678")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(Gender.HOMBRE)
                .skillLevel(SkillLevel.PRINCIPIANTE)
                .profileComplete(true)
                .interests(interests)
                .build();

        userRepository.save(user);
    }
}
