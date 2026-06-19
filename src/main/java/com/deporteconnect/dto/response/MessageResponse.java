package com.deporteconnect.dto.response;

import com.deporteconnect.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long activityId;
    private UserBriefResponse sender;
    private String content;
    private String type;
    private LocalDateTime sentAt;

    public static MessageResponse from(Message m) {
        return MessageResponse.builder()
            .id(m.getId())
            .activityId(m.getActivity().getId())
            .sender(UserBriefResponse.from(m.getSender()))
            .content(m.getContent())
            .type(m.getType().name())
            .sentAt(m.getSentAt())
            .build();
    }
}
