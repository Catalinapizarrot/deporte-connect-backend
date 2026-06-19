package com.deporteconnect.service;

import com.deporteconnect.dto.request.SendMessageRequest;
import com.deporteconnect.dto.response.MessageResponse;
import com.deporteconnect.exception.BusinessException;
import com.deporteconnect.exception.ResourceNotFoundException;
import com.deporteconnect.model.Activity;
import com.deporteconnect.model.Message;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.ActivityRepository;
import com.deporteconnect.repository.MessageRepository;
import com.deporteconnect.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ActivityRepository activityRepository;
    private final ParticipationRepository participationRepository;

    /** Obtiene los mensajes del chat de una actividad. */
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(User user, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        assertIsParticipant(user, activity);

        return messageRepository.findByActivityOrderBySentAtAsc(activity).stream()
            .map(MessageResponse::from)
            .collect(Collectors.toList());
    }

    /** EnvÃ­a un mensaje al chat de una actividad. */
    @Transactional
    public MessageResponse sendMessage(User user, Long activityId, SendMessageRequest request) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        assertIsParticipant(user, activity);

        Message message = Message.builder()
            .activity(activity)
            .sender(user)
            .content(request.getContent().trim())
            .type(Message.MessageType.USER)
            .build();

        message = messageRepository.save(message);
        return MessageResponse.from(message);
    }

    /** Solo los inscritos pueden leer o enviar mensajes en el chat. */
    private void assertIsParticipant(User user, Activity activity) {
        if (!participationRepository.existsByUserAndActivity(user, activity)) {
            throw new BusinessException("Debes estar inscrito para acceder a este chat");
        }
    }
}
