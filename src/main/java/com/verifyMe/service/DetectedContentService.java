package com.verifyMe.service;

import com.verifyMe.DTO.DetectedContentResponseDTO;
import com.verifyMe.Entity.DetectedContent;
import com.verifyMe.Repository.DetectedContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class DetectedContentService implements DetectedContentServiceI{

    @Autowired
    private DetectedContentRepository dcr;

    @Override
    public List<DetectedContent> getAllDetectedContent() {
        return dcr.findAll();
    }

    @Override
    public Optional<DetectedContent> getDetectedContentById(Long id) {
        return dcr.findById(id);
    }

    @Override
    public DetectedContent saveDetectedContent(DetectedContent detectedContent) {
        return dcr.save(detectedContent);
    }

    @Override
    public void deleteDetectedContent(Long id) {
        dcr.deleteById(id);
    }
    
    @Override
    public DetectedContentResponseDTO getContentsByUsername(String username){
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
    	List<DetectedContent> contentsOfMonth = this.dcr.findCurrentMonthContents(username, startOfMonth, endOfMonth);
    	List<DetectedContent> contentsOfPreviousMonth = this.dcr.findPreviousMonthsContents(username, startOfMonth);
        return new DetectedContentResponseDTO(contentsOfMonth, contentsOfPreviousMonth);

    }
    
}

