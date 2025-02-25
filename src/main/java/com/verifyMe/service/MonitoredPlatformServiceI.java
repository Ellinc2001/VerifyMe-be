package com.verifyMe.service;

import java.util.List;

import com.verifyMe.Entity.MonitoredPlatform;

public interface MonitoredPlatformServiceI {

	List<MonitoredPlatform> getPlatformsByUser(String userId) throws Exception;
	MonitoredPlatform addPlatform(String platformName, Long userId);
	void removePlatform(Long platformId);
}
