package com.deporteconnect.service;

import com.deporteconnect.dto.request.LoginRequest;
import com.deporteconnect.dto.request.RegisterRequest;
import com.deporteconnect.dto.response.AuthResponse;
import com.deporteconnect.exception.BusinessException;
import com.deporteconnect.model.SkillLevel;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.UserRepository;
import com.deporteconnect.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo ya estÃ¡ registrado");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .skillLevel(SkillLevel.PRINCIPIANTE)
                .profileComplete(false)
                .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileComplete(false)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Email o contraseÃ±a incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email o contraseÃ±a incorrectos");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        // El perfil estÃ¡ completo si ya tiene nombre, fecha de nacimiento Y gÃ©nero
        boolean profileComplete = user.getFullName() != null
                && !user.getFullName().isBlank()
                && user.getBirthDate() != null
                && user.getGender() != null;

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileComplete(profileComplete)
                .build();
    }
}