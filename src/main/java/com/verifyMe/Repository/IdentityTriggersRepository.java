package com.verifyMe.Repository;

import com.verifyMe.Entity.IdentityTriggers;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IdentityTriggersRepository extends JpaRepository<IdentityTriggers, Long> {
    List<IdentityTriggers> findByUserId(Long userId);
    List<IdentityTriggers> findByUserIdAndPlatform(Long userId, String platform);
}
