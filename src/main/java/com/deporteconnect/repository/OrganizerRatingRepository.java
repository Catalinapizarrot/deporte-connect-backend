package com.deporteconnect.repository;

import com.deporteconnect.model.OrganizerRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRatingRepository extends JpaRepository<OrganizerRating, Long> {
    boolean existsByActivityIdAndRaterId(Long activityId, Long raterId);
}
