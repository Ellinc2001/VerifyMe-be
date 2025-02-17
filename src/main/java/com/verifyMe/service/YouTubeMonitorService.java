package com.verifyMe.service;

import com.verifyMe.Entity.IdentityTriggers;
import com.verifyMe.Entity.User;
import com.verifyMe.Repository.IdentityTriggersRepository;
import com.verifyMe.Repository.UserRepository;
import com.verifyMe.Utils.FaceEmbeddingUtil;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class YouTubeMonitorService {

    private final String API_KEY = "LA_TUA_API_KEY";
    private final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&order=date&maxResults=10&q=";

    @Autowired
    private IdentityTriggersRepository triggersRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<String> cercaVideoPerUtente(Long userId) {
        List<String> videoSospetti = new ArrayList<>();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        User user = userOptional.get();

        // 🔹 Prendi l'ultima data di scansione o usa una data iniziale se è la prima volta
        LocalDateTime lastScan = user.getLastScanDateYt() != null ? user.getLastScanDateYt() : LocalDateTime.now().minusDays(7);
        String lastScanISO = lastScan.format(DateTimeFormatter.ISO_DATE_TIME) + "Z"; // Formato richiesto da YouTube

        // 🔹 Recuperiamo gli Identity Triggers associati
        List<IdentityTriggers> triggersList = triggersRepository.findByUserId(userId);

        for (IdentityTriggers trigger : triggersList) {
            List<String> queryKeywords = new ArrayList<>();
            queryKeywords.add(trigger.getName());
            queryKeywords.addAll(trigger.getAliases());
            queryKeywords.addAll(trigger.getKeywords());

            for (String keyword : queryKeywords) {
                videoSospetti.addAll(cercaVideo(user, keyword, lastScanISO));
            }
        }

        // 🔹 Dopo la scansione, aggiorna la data nel DB
        user.setLastScanDateYt(LocalDateTime.now());
        userRepository.save(user);

        return videoSospetti;
    }


    private List<String> cercaVideo(User user, String keyword, String publishedAfter) {
        List<String> videoSospetti = new ArrayList<>();
        try {
            String apiUrl = SEARCH_URL + keyword + "&publishedAfter=" + publishedAfter + "&key=" + API_KEY;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            for (JsonNode item : root.get("items")) {
                String videoId = item.get("id").get("videoId").asText();
                String titolo = item.get("snippet").get("title").asText();
                String descrizione = item.get("snippet").get("description").asText();

                boolean isSuspicious = false;

                if (titolo.toLowerCase().contains(keyword.toLowerCase()) ||
                    descrizione.toLowerCase().contains(keyword.toLowerCase()) ||
                    verificaTags(keyword, videoId) ||
                    verificaCommenti(keyword, videoId)) {
                    isSuspicious = true;
                }

                // 🔍 Controlliamo se la miniatura contiene il volto dell'utente
                if (verificaMiniatura(videoId, user)) {
                    isSuspicious = true;
                }

                if (isSuspicious) {
                    videoSospetti.add("⚠️ Video sospetto: " + titolo + " (https://www.youtube.com/watch?v=" + videoId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoSospetti;
    }


    private boolean verificaMiniatura(String videoId, User user) {
        try {
            // 🔹 Scarica la miniatura del video
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
            String videoThumbnailPath = "temp_thumbnail.jpg";

            BufferedImage image = ImageIO.read(new URL(thumbnailUrl));
            File outputFile = new File(videoThumbnailPath);
            ImageIO.write(image, "jpg", outputFile);

            // 🔹 Recupera l'impronta biometrica dell'utente dal DB
            byte[] userEmbedding = user.getEncryptedFaceEmbedding();
            if (userEmbedding == null) {
                System.out.println("❌ Nessun embedding salvato per l'utente.");
                return false;
            }

            // 🔹 Confronto tra l'impronta biometrica salvata e la miniatura del video
            boolean isMatch = FaceEmbeddingUtil.compareFaceEmbeddings(userEmbedding, videoThumbnailPath);

            // 🔹 Rimuovi file temporanei dopo il confronto
            outputFile.delete();

            return isMatch;

        } catch (Exception e) {
            System.err.println("❌ Errore nel confronto facciale: " + e.getMessage());
        }
        return false;
    }


    // 🔹 Verifica se un video contiene il nome nei Tag
    private boolean verificaTags(String keyword, String videoId) {
        try {
            String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + API_KEY;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode tags = root.get("items").get(0).get("snippet").get("tags");

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

    // 🔹 Verifica se un video contiene il nome nei Commenti
    private boolean verificaCommenti(String keyword, String videoId) {
        try {
            String apiUrl = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=" + videoId + "&maxResults=100&key=" + API_KEY;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode commenti = root.get("items");

            if (commenti != null) {
                for (JsonNode commento : commenti) {
                    String testoCommento = commento.get("snippet").get("topLevelComment").get("snippet").get("textDisplay").asText();
                    if (testoCommento.toLowerCase().contains(keyword.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
