package com.deporteconnect.service;

import com.deporteconnect.dto.response.SportResponse;
import com.deporteconnect.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportService {

    private final SportRepository sportRepository;

    @Transactional(readOnly = true)
    public List<SportResponse> getAll() {
        return sportRepository.findAll().stream()
            .map(SportResponse::from)
            .collect(Collectors.toList());
    }
}
