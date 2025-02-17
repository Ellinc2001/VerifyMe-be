package com.verifyMe.Entity;

import jakarta.persistence.*;
import lombok.*;
import com.verifyMe.Utils.FaceEmbeddingUtil;
import com.verifyMe.Utils.FaceEncryptionUtil;
import java.time.LocalDateTime;

import java.util.Set;

import javax.crypto.SecretKey;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "users")
public class User {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "surname")
    private String surname;
    
    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IdentityTriggers> identityTriggers;  // Lista di Identity Triggers
    
    @Column(nullable = false)
    private int checkFrequencyHours = 24;  // Frequenza di controllo in ore (default: ogni 24 ore)
    
    @Lob
    @Column(name = "face_embedding")
    @Basic(fetch = FetchType.LAZY)
    private byte[] encryptedFaceEmbedding;

    
    @Column(name = "last_scan_date_yt")
    private LocalDateTime lastScanDateYt;
    
    @Column(name = "last_scan_date_ig")
    private LocalDateTime lastScanDateIg;

    
    public Long getId() {
    	return this.id;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public String getPassword() {
    	return this.password;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    

    // Getter e Setter
    public int getCheckFrequencyHours() {
        return checkFrequencyHours;
    }

    public void setCheckFrequencyHours(int checkFrequencyHours) {
        this.checkFrequencyHours = checkFrequencyHours;
    }

    public void setFaceEmbedding(float[] embedding, SecretKey key) throws Exception {
        byte[] rawData = FaceEmbeddingUtil.floatArrayToByteArray(embedding);
        this.encryptedFaceEmbedding = FaceEncryptionUtil.encrypt(rawData, key);
    }

    public float[] getFaceEmbedding(SecretKey key) throws Exception {
        byte[] decryptedData = FaceEncryptionUtil.decrypt(this.encryptedFaceEmbedding, key);
        return FaceEmbeddingUtil.byteArrayToFloatArray(decryptedData);
    }
    
    public void setEncryptedFaceEmbedding(byte[] encryptedFaceEmbedding) {
        this.encryptedFaceEmbedding = encryptedFaceEmbedding;
    }

    // ✅ Metodo per ottenere il face embedding crittografato
    public byte[] getEncryptedFaceEmbedding() {
        return this.encryptedFaceEmbedding;
    }
    
    public LocalDateTime getLastScanDateYt() {
    	return this.lastScanDateYt;
    }
    
    public void setLastScanDateYt(LocalDateTime lastScanDate) {
    	this.lastScanDateYt = lastScanDate;
    }
    
    public LocalDateTime getLastScanDateIg() {
    	return this.lastScanDateIg;
    }
    
    public void setLastScanDateIg(LocalDateTime lastScanDateTimeIg) {
    	this.lastScanDateIg = lastScanDateTimeIg;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getSurname() {
    	return this.surname;
    }
    
    public void setSurname(String surname) {
    	this.surname = surname;
    }


}
