package com.deporteconnect.service;

import com.deporteconnect.dto.request.UpdateProfileRequest;
import com.deporteconnect.dto.response.UserResponse;
import com.deporteconnect.exception.BusinessException;
import com.deporteconnect.exception.ResourceNotFoundException;
import com.deporteconnect.model.Sport;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.SportRepository;
import com.deporteconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SportRepository sportRepository;

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        user.getInterests().size();
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.getInterests().size();
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        User user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // ValidaciÃ³n: mayor de edad
        int age = Period.between(request.getBirthDate(), LocalDate.now()).getYears();
        if (age < 18) {
            throw new BusinessException("Debes ser mayor de 18 aÃ±os para usar la aplicaciÃ³n");
        }

        // ValidaciÃ³n: gÃ©nero obligatorio
        if (request.getGender() == null) {
            throw new BusinessException("El gÃ©nero es obligatorio");
        }

        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone().trim());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());

        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getBio() != null) user.setBio(request.getBio());

        if (request.getInterestIds() != null) {
            Set<Sport> interests = new HashSet<>();
            for (Long sportId : request.getInterestIds()) {
                Sport sport = sportRepository.findById(sportId)
                    .orElseThrow(() -> new ResourceNotFoundException("Deporte no encontrado: " + sportId));
                interests.add(sport);
            }
            user.setInterests(interests);
        }

        user.setProfileComplete(true);

        user = userRepository.save(user);
        user.getInterests().size();
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateAvatar(User currentUser, String avatarUrl) {
        User user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setAvatarUrl(avatarUrl);
        user = userRepository.save(user);
        user.getInterests().size();
        return UserResponse.from(user);
    }
}
