package com.verifyMe.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "detected_content")
public class DetectedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Utente associato al contenuto segnalato

    @Column(nullable = false)
    private String platform; // Es: "YOUTUBE", "INSTAGRAM", "TIKTOK"

    @Column(nullable = false)
    private String contentId; // Es: videoId di YouTube o postId di Instagram

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String url; // Link al contenuto

    @Column(nullable = false)
    private LocalDateTime detectedAt; // Data della segnalazione

    @Column(nullable = false)
    private Boolean faceMatch; // Indica se il volto dell'utente è stato rilevato nella miniatura

    @Column(nullable = false)
    private Boolean keywordMatch; // Indica se il contenuto è stato individuato tramite parole chiave
    
    @Column(nullable = false)
    private Boolean isCritical;

    public DetectedContent() {
    	
    }
    
    public DetectedContent(User user, String platform, String contentId, String title, String description, 
            String url, boolean faceMatch, boolean keywordMatch) {
		this.user = user;
		this.platform = platform;
		this.contentId = contentId;
		this.title = title;
		this.description = description;
		this.url = url;
		this.detectedAt = LocalDateTime.now();
		this.faceMatch = faceMatch;
		this.keywordMatch = keywordMatch;
    }
    
    public User getUser() {
    	return this.user;
    }
    
    public void setUser(User user) {
    	this.user = user;
    }
    
    public String getPlatform() {
    	return this.platform;
    }
    
    public void setPlatform(String platform) {
    	this.platform = platform;
    }
     
    public String getContentId() {
    	return this.contentId;
    }
    
    public void setContentId(String contentId) {
    	this.contentId = contentId;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getDescription() {
    	return this.description;
    }
    
    public void setDescription(String description) {
    	this.description = description;
    }
    
    public String getUrl() {
    	return this.url;
    }
    
    public void setUrl(String url) {
    	this.url = url;
    }
    
    public LocalDateTime getDetectedAt() {
    	return this.detectedAt;
    }
    
    public void setDetectedAt(LocalDateTime detectedAt) {
    	this.detectedAt = detectedAt;
    }
    
    public Boolean getFaceMatch() {
    	return this.faceMatch;
    }
    
    public void setFaceMatch(Boolean faceMatch) {
    	this.faceMatch = faceMatch;
    }
    
    public Boolean getKeyWordsMatch() {
    	return this.keywordMatch;
    }
    
    public void setKeyWordsMatch(Boolean keyWordMatch) {
    	this.keywordMatch = keyWordMatch;
    }
    
    public Boolean getIsCritical() {
    	return this.isCritical;
    }
    
    public void setIsCritical(Boolean isCritical) {
    	this.isCritical = isCritical;
    }
}
