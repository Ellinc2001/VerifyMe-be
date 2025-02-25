package com.verifyMe.Repository;

import com.verifyMe.Entity.MonitoredPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonitoredPlatformRepository extends JpaRepository<MonitoredPlatform, Long> {

    List<MonitoredPlatform> findByUserId(Long userId);
    boolean existsByPlatformNameAndUserId(String platformName, Long userId);
}
