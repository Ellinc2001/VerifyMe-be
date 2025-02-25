package com.verifyMe.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(name = "monitored_platforms")
public class MonitoredPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String platformName; // Es. YouTube, Instagram, TikTok

    @Column(nullable = false)
    private Boolean isActive = true; // Stato del monitoraggio

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Utente che monitora questa piattaforma
    
    Long getId() {
    	return this.id;
    }
    
    void setId(Long id) {
    	this.id = id;
    }
    
    public String getPlatformName() {
    	return this.platformName;
    }
    
    public void setPlatformName(String platformName) {
    	this.platformName = platformName;
    }
    
    public Boolean getIsActive() {
    	return this.isActive;
    }
    
    public void setIsActive(Boolean isActive) {
    	this.isActive = isActive;
    }
    
    public User getUser() {
    	return this.user;
    }
    
    public void setUser(User User) {
    	this.user = user;
    }

}
