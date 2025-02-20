package com.verifyMe.DTO;

import com.verifyMe.Entity.DetectedContent;
import java.util.List;

public class DetectedContentResponseDTO {
    private List<DetectedContent> contentsOfMonth;
    private List<DetectedContent> contentsOfPreviousMonth;

    public DetectedContentResponseDTO(List<DetectedContent> contentsOfMonth, List<DetectedContent> contentsOfPreviousMonth) {
        this.contentsOfMonth = contentsOfMonth;
        this.contentsOfPreviousMonth = contentsOfPreviousMonth;
    }

    public List<DetectedContent> getContentsOfMonth() {
        return contentsOfMonth;
    }

    public void setContentsOfMonth(List<DetectedContent> contentsOfMonth) {
        this.contentsOfMonth = contentsOfMonth;
    }

    public List<DetectedContent> getContentsOfPreviousMonth() {
        return contentsOfPreviousMonth;
    }

    public void setContentsOfPreviousMonth(List<DetectedContent> contentsOfPreviousMonth) {
        this.contentsOfPreviousMonth = contentsOfPreviousMonth;
    }
}
