package com.deporteconnect.dto.response;

import com.deporteconnect.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** VersiÃ³n compacta del usuario para mostrar en listas, avatares, etc. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBriefResponse {
    private Long id;
    private String fullName;
    private String avatarUrl;

    public static UserBriefResponse from(User user) {
        return UserBriefResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }
}
