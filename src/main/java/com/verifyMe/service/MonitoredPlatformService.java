package com.verifyMe.service;

import com.verifyMe.Entity.MonitoredPlatform;
import com.verifyMe.Repository.MonitoredPlatformRepository;
import com.verifyMe.Entity.User;
import com.verifyMe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoredPlatformService implements MonitoredPlatformServiceI {

    @Autowired
    private MonitoredPlatformRepository platformRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<MonitoredPlatform> getPlatformsByUser(String username) throws Exception {
    	Optional<User> user = userRepository.findByUsername(username);
    	if(user.isEmpty()) {
    		throw new Exception("User inesistente");
    	}
    	Long id = user.get().getId();
        return platformRepository.findByUserId(id);
    }

    @Override
    public MonitoredPlatform addPlatform(String platformName, Long userId) {
        if (platformRepository.existsByPlatformNameAndUserId(platformName, userId)) {
            throw new RuntimeException("Platform already monitored by this user.");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        MonitoredPlatform platform = new MonitoredPlatform();
        platform.setPlatformName(platformName);
        platform.setUser(user.get());
        platform.setIsActive(true);

        return platformRepository.save(platform);
    }

    @Override
    public void removePlatform(Long platformId) {
        platformRepository.deleteById(platformId);
    }
}
