package com.verifyMe.Repository;

import com.verifyMe.Entity.DetectedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetectedContentRepository extends JpaRepository<DetectedContent, Long> {
    List<DetectedContent> findByUserId(Long userId);
    boolean existsByContentIdAndUserId(String contentId, Long userId);
    int countByPlatform(String platform);
    List<DetectedContent> findByUserUsername(String username);
    List<DetectedContent> findByUserUsernameAndDetectedAtBetween(String username, LocalDateTime startDate, LocalDateTime endDate);
    
    // Contenuti di questo mese
    @Query("SELECT d FROM DetectedContent d WHERE d.user.username = :username " +
           "AND d.detectedAt BETWEEN :startOfMonth AND :endOfMonth")
    List<DetectedContent> findCurrentMonthContents(
            @Param("username") String username,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );

    // Contenuti dei mesi precedenti
    @Query("SELECT d FROM DetectedContent d WHERE d.user.username = :username " +
           "AND d.detectedAt < :startOfMonth")
    List<DetectedContent> findPreviousMonthsContents(
            @Param("username") String username,
            @Param("startOfMonth") LocalDateTime startOfMonth
    );
}
