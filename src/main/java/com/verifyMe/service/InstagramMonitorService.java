package com.verifyMe.service;

import com.verifyMe.Entity.DetectedContent;
import com.verifyMe.Entity.IdentityTriggers;
import com.verifyMe.Entity.User;
import com.verifyMe.Repository.DetectedContentRepository;
import com.verifyMe.Repository.IdentityTriggersRepository;
import com.verifyMe.Repository.UserRepository;
import com.verifyMe.Utils.FaceEmbeddingUtil;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InstagramMonitorService {

    private final String ACCESS_TOKEN = "LA_TUA_INSTAGRAM_ACCESS_TOKEN";
    private final String SEARCH_URL = "https://graph.instagram.com/me/media?fields=id,caption,media_url,timestamp,permalink&access_token=";
    private final String COMMENTS_URL = "https://graph.instagram.com/{post_id}/comments?fields=text&access_token=";
    private final String TAGS_URL = "https://graph.instagram.com/{post_id}?fields=tags&access_token=";

    @Autowired
    private IdentityTriggersRepository triggersRepository;
    
    @Autowired
    private DetectedContentRepository detectedContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<String> cercaPostPerUtente(Long userId) {
        List<String> postSospetti = new ArrayList<>();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        User user = userOptional.get();

        // 🔹 Prendi l'ultima data di scansione o usa una data iniziale
        LocalDateTime lastScan = user.getLastScanDateIg() != null ? user.getLastScanDateIg() : LocalDateTime.now().minusDays(7);
        String lastScanISO = lastScan.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";

        // 🔹 Recuperiamo gli Identity Triggers associati
        List<IdentityTriggers> triggersList = triggersRepository.findByUserIdAndPlatform(userId, "INSTAGRAM");

        for (IdentityTriggers trigger : triggersList) {
            List<String> queryKeywords = new ArrayList<>();
            queryKeywords.add(trigger.getName());
            queryKeywords.addAll(trigger.getAliases());
            queryKeywords.addAll(trigger.getKeywords());
            queryKeywords.addAll(trigger.getTags()); // 🔹 Aggiungiamo anche gli hashtag

            for (String keyword : queryKeywords) {
                postSospetti.addAll(cercaPost(user, keyword, lastScanISO));
            }
        }

        // 🔹 Dopo la scansione, aggiorna la data nel DB
        user.setLastScanDateIg(LocalDateTime.now());
        userRepository.save(user);

        return postSospetti;
    }

    private List<String> cercaPost(User user, String keyword, String publishedAfter) {
        List<String> postSospetti = new ArrayList<>();
        try {
            String apiUrl = SEARCH_URL + ACCESS_TOKEN;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            for (JsonNode item : root.get("data")) {
                String postId = item.get("id").asText();
                String caption = item.has("caption") ? item.get("caption").asText() : "";
                String mediaUrl = item.has("media_url") ? item.get("media_url").asText() : "";
                String permalink = item.get("permalink").asText();
                String timestamp = item.get("timestamp").asText();

                // 🔹 Evita duplicati: se il post è già stato segnalato, skippa
                if (detectedContentRepository.existsByContentIdAndUserId(postId, user.getId())) {
                    continue;
                }

                boolean isSuspicious = false;
                boolean keywordMatch = false;
                boolean faceMatch = false;

                if (timestamp.compareTo(publishedAfter) <= 0) {
                    continue;
                }

                // 🔹 Controllo su caption (parole chiave)
                if (caption.toLowerCase().contains(keyword.toLowerCase())) {
                    isSuspicious = true;
                    keywordMatch = true;
                }

                // 🔹 Controllo sui tag del post
                if (verificaTags(postId, keyword)) {
                    isSuspicious = true;
                    keywordMatch = true;
                }

                // 🔹 Controllo sui commenti
                if (verificaCommenti(postId, keyword)) {
                    isSuspicious = true;
                    keywordMatch = true;
                }

                // 🔹 Controllo sulla miniatura dell'immagine del post
                if (verificaImmagine(mediaUrl, user)) {
                    isSuspicious = true;
                    faceMatch = true;
                }

                if (isSuspicious) {
                    // 🔹 Salva la segnalazione nel database
                    DetectedContent detectedContent = new DetectedContent(
                            user, "INSTAGRAM", postId, caption, caption, permalink, faceMatch, keywordMatch
                    );
                    detectedContentRepository.save(detectedContent);

                    postSospetti.add("⚠️ Post sospetto: " + caption + " (" + permalink + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postSospetti;
    }


    private boolean verificaTags(String postId, String keyword) {
        try {
            String apiUrl = TAGS_URL.replace("{post_id}", postId) + ACCESS_TOKEN;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode tags = root.get("tags");

            if (tags != null) {
                for (JsonNode tag : tags) {
                    if (tag.asText().toLowerCase().contains(keyword.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean verificaCommenti(String postId, String keyword) {
        try {
            String apiUrl = COMMENTS_URL.replace("{post_id}", postId) + ACCESS_TOKEN;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode comments = root.get("data");

            if (comments != null) {
                for (JsonNode comment : comments) {
                    String text = comment.get("text").asText();
                    if (text.toLowerCase().contains(keyword.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean verificaImmagine(String mediaUrl, User user) {
        try {
            // 🔹 Scarica l'immagine del post
            BufferedImage image = ImageIO.read(new URL(mediaUrl));
            File outputFile = new File("temp_instagram_image.jpg");
            ImageIO.write(image, "jpg", outputFile);

            // 🔹 Recupera l'impronta biometrica dell'utente
            byte[] userEmbedding = user.getEncryptedFaceEmbedding();
            if (userEmbedding == null) {
                return false;
            }

            // 🔹 Confronto tra l'impronta biometrica salvata e l'immagine del post
            boolean isMatch = FaceEmbeddingUtil.compareFaceEmbeddings(userEmbedding, outputFile.getAbsolutePath());

            // 🔹 Rimuovi file temporanei dopo il confronto
            outputFile.delete();

            return isMatch;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
