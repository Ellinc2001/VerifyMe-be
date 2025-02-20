package com.verifyMe.service;

import java.util.List;

import com.verifyMe.Entity.IdentityTriggers;

public interface IdentityTriggerServiceI {

	List<IdentityTriggers> getTriggersByUserId(Long userId);

	IdentityTriggers updateTrigger(Long triggerId, IdentityTriggers updatedTrigger);

	IdentityTriggers createTrigger(Long userId, IdentityTriggers trigger);

	void deleteTrigger(Long triggerId);

}
