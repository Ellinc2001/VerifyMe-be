package com.verifyMe.Repository;

import com.verifyMe.Entity.DetectedContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DetectedContentRepository extends JpaRepository<DetectedContent, Long> {
    List<DetectedContent> findByUserId(Long userId);
    boolean existsByContentIdAndUserId(String contentId, Long userId);
    int countByPlatform(String platform);
    List<DetectedContent> findByUserUsername(String username);
    List<DetectedContent> findByUserUsernameAndDetectedAtBetween(String username, LocalDateTime startDate, LocalDateTime endDate);
}
