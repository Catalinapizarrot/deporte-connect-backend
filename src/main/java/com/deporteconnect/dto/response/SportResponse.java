package com.deporteconnect.dto.response;

import com.deporteconnect.model.Sport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SportResponse {
    private Long id;
    private String name;
    private String iconKey;
    private String color;

    public static SportResponse from(Sport sport) {
        return SportResponse.builder()
            .id(sport.getId())
            .name(sport.getName())
            .iconKey(sport.getIconKey())
            .color(sport.getColor())
            .build();
    }
}
