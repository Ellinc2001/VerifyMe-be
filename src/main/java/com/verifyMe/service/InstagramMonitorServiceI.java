package com.verifyMe.service;

import java.util.List;

import com.verifyMe.Entity.User;

public interface InstagramMonitorServiceI {

	List<String> cercaPostPerUtente(Long userId);

}
