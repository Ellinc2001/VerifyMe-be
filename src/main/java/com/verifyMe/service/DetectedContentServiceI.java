package com.verifyMe.service;

import java.util.List;
import java.util.Optional;

import com.verifyMe.DTO.DetectedContentResponseDTO;
import com.verifyMe.Entity.DetectedContent;

public interface DetectedContentServiceI {

	List<DetectedContent> getAllDetectedContent();

	Optional<DetectedContent> getDetectedContentById(Long id);

	DetectedContent saveDetectedContent(DetectedContent detectedContent);

	void deleteDetectedContent(Long id);

	DetectedContentResponseDTO getContentsByUsername(String username);

}
