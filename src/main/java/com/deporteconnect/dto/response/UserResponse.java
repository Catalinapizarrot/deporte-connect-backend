package com.deporteconnect.dto.response;

import com.deporteconnect.model.Gender;
import com.deporteconnect.model.SkillLevel;
import com.deporteconnect.model.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
    private String avatarUrl;
    private String bio;
    private SkillLevel skillLevel;
    private Boolean profileComplete;
    private List<SportResponse> interests;
    private LocalDateTime createdAt;   // â­ para "Miembro desde"
    private BigDecimal participantRating;       // â­ NUEVO
    private Integer participantRatingCount;     // â­ NUEVO

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .skillLevel(user.getSkillLevel())
                .profileComplete(user.getProfileComplete())
                .interests(user.getInterests() == null ? List.of()
                        : user.getInterests().stream()
                        .map(SportResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .participantRating(user.getParticipantRating())          // â­ NUEVO
                .participantRatingCount(user.getParticipantRatingCount()) // â­ NUEVO
                .build();
    }
}