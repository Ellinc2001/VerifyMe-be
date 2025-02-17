package com.verifyMe.service;

import com.verifyMe.Entity.DetectedContent;
import com.verifyMe.Entity.User;
import com.verifyMe.Repository.DetectedContentRepository;
import com.verifyMe.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InsightsService {

    @Autowired
    private DetectedContentRepository detectedContentRepository;
    
    @Autowired 
    private UserRepository usr;

    public Map<String, Integer> getInsights() {
        Map<String, Integer> insights = new HashMap<>();

        insights.put("YouTube", detectedContentRepository.countByPlatform("YOUTUBE"));
        insights.put("Instagram", detectedContentRepository.countByPlatform("INSTAGRAM"));
        insights.put("TikTok", detectedContentRepository.countByPlatform("TIKTOK"));

        return insights;
    }
    
    
    public Map<String, Map<String, Integer>> getInsightsForUser(String username) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1);
        LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
        LocalDateTime endOfPreviousMonth = startOfCurrentMonth.minusSeconds(1);

        // 🔹 Recupera segnalazioni del mese attuale
        List<DetectedContent> currentMonthData = detectedContentRepository.findByUserUsernameAndDetectedAtBetween(username, startOfCurrentMonth, now);
        
        // 🔹 Recupera segnalazioni del mese precedente
        List<DetectedContent> previousMonthData = detectedContentRepository.findByUserUsernameAndDetectedAtBetween(username, startOfPreviousMonth, endOfPreviousMonth);

        Map<String, Map<String, Integer>> insights = new HashMap<>();
        Map<String, Integer> currentMonthCounts = new HashMap<>();
        Map<String, Integer> previousMonthCounts = new HashMap<>();
        
        int totalCurrent = 0;
        int totalPrevious = 0;
        int totalCriticalCurrent = 0;
        int totalCriticalPrevious = 0;

        // 🔹 Conta le segnalazioni del mese attuale
        for (DetectedContent content : currentMonthData) {
            String platform = content.getPlatform();
            boolean isCritical = content.getFaceMatch() || content.getKeyWordsMatch();

            insights.putIfAbsent(platform, new HashMap<>());
            currentMonthCounts.put(platform, currentMonthCounts.getOrDefault(platform, 0) + 1);
            insights.get(platform).put("total", currentMonthCounts.get(platform));

            if (isCritical) {
                insights.get(platform).put("critical", insights.get(platform).getOrDefault("critical", 0) + 1);
                totalCriticalCurrent++;
            }

            totalCurrent++; // Incrementa il totale delle segnalazioni attuali
        }

        // 🔹 Conta le segnalazioni del mese precedente
        for (DetectedContent content : previousMonthData) {
            String platform = content.getPlatform();
            previousMonthCounts.put(platform, previousMonthCounts.getOrDefault(platform, 0) + 1);
            if (content.getFaceMatch() || content.getKeyWordsMatch()) {
                totalCriticalPrevious++;
            }
            totalPrevious++; // Incrementa il totale delle segnalazioni precedenti
        }

        // 🔹 Calcolo della variazione percentuale per ogni piattaforma
        for (String platform : currentMonthCounts.keySet()) {
            int current = currentMonthCounts.getOrDefault(platform, 0);
            int previous = previousMonthCounts.getOrDefault(platform, 0);

            int percentageChange = ((current - previous) * 100) / Math.max(previous, 1); // 💡 Uso max(previous,1) per evitare 1000%
            
            insights.get(platform).put("percentageChange", percentageChange);
        }

        // 🔹 Calcolo della variazione percentuale totale
        int totalPercentageChange = ((totalCurrent - totalPrevious) * 100) / Math.max(totalPrevious, 1);
        int totalCriticalPercentageChange = ((totalCriticalCurrent - totalCriticalPrevious) * 100) / Math.max(totalCriticalPrevious, 1);

        // 🔹 Aggiungiamo un campo speciale per il totale
        Map<String, Integer> totalInsights = new HashMap<>();
        totalInsights.put("total", totalCurrent);
        totalInsights.put("previousTotal", totalPrevious);
        totalInsights.put("percentageChange", totalPercentageChange);
        totalInsights.put("critical", totalCriticalCurrent);
        totalInsights.put("criticalPercentageChange", totalCriticalPercentageChange);

        insights.put("TOTAL", totalInsights);

        // 🔹 Debug per controllare i dati generati
        System.out.println("Insights generati: " + insights);

        return insights;
    }

}
