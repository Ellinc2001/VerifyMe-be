package com.verifyMe.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "identity_triggers")
public class IdentityTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // Nome principale dell'utente

    @Column(nullable = false)
    private String platform;  // 🔹 Specifica il social network (YouTube, Instagram, TikTok...)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "identity_aliases", joinColumns = @JoinColumn(name = "identity_trigger_id"))
    @Column(name = "alias")
    private List<String> aliases;  // Possibili varianti del nome

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "identity_keywords", joinColumns = @JoinColumn(name = "identity_trigger_id"))
    @Column(name = "keyword")
    private List<String> keywords;  // Parole chiave sospette

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "identity_tags", joinColumns = @JoinColumn(name = "identity_trigger_id"))
    @Column(name = "tag")
    private List<String> tags;  // Hashtag o tag associati

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "identity_suspect_channels", joinColumns = @JoinColumn(name = "identity_trigger_id"))
    @Column(name = "channel")
    private List<String> suspectChannels;  // Canali sospetti da monitorare

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Collegamento all'utente proprietario di questi triggers

    // ✅ Costruttore Vuoto
    public IdentityTriggers() {}

    // ✅ Costruttore Completo
    public IdentityTriggers(String name, String platform, List<String> aliases, List<String> keywords, List<String> tags, List<String> suspectChannels, User user) {
        this.name = name;
        this.platform = platform;
        this.aliases = aliases;
        this.keywords = keywords;
        this.tags = tags;
        this.suspectChannels = suspectChannels;
        this.user = user;
    }

    // ✅ Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getSuspectChannels() {
        return suspectChannels;
    }

    public void setSuspectChannels(List<String> suspectChannels) {
        this.suspectChannels = suspectChannels;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
