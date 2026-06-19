package com.deporteconnect.repository;

import com.deporteconnect.model.Activity;
import com.deporteconnect.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /** Mensajes de una actividad, ordenados cronolÃ³gicamente. */
    List<Message> findByActivityOrderBySentAtAsc(Activity activity);
}
