package com.verifyMe.scheduler;

import com.verifyMe.Entity.User; 
import com.verifyMe.Repository.UserRepository;
import com.verifyMe.service.YouTubeMonitorService;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class YouTubeMonitorScheduler {

    private final YouTubeMonitorService youTubeMonitorService;
    private final UserRepository userRepository;
    private final ScheduledExecutorService scheduler;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public YouTubeMonitorScheduler(YouTubeMonitorService youTubeMonitorService, UserRepository userRepository) {
        this.youTubeMonitorService = youTubeMonitorService;
        this.userRepository = userRepository;
        this.scheduler = Executors.newScheduledThreadPool(10); // Esegue massimo 10 utenti in parallelo
    }

    // 🔹 Avvia il monitoraggio per ogni utente quando l'applicazione si avvia
    @PostConstruct
    public void startMonitoring() {
        List<User> utenti = userRepository.findAll();
        for (User user : utenti) {
            scheduleUserMonitoring(user);
        }
    }

    // 🔹 Pianifica il monitoraggio per un utente in base al suo intervallo
    private void scheduleUserMonitoring(User user) {
        long interval = TimeUnit.HOURS.toSeconds(user.getCheckFrequencyHours());

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> monitoraUtente(user),
            0, // Esegui subito il primo controllo
            interval, // Successivamente ogni X ore specificate dall'utente
            TimeUnit.SECONDS
        );

        // 🔹 Salviamo il task per permettere aggiornamenti dinamici
        scheduledTasks.put(user.getId(), future);
    }

    // 🔹 Monitora un singolo utente
    private void monitoraUtente(User user) {
        try {
            System.out.println("🔍 Avvio monitoraggio per: " + user.getUsername());
            List<String> risultati = youTubeMonitorService.cercaVideoPerUtente(user.getId());

            if (!risultati.isEmpty()) {
                inviaNotifica(user, risultati);
            }

        } catch (Exception e) {
            System.err.println("❌ Errore nel monitoraggio di " + user.getUsername() + ": " + e.getMessage());
        }
    }

    // 🔹 Invia una notifica all'utente se vengono trovati video sospetti
    private void inviaNotifica(User user, List<String> risultati) {
        System.out.println("📧 Notifica inviata a " + user.getUsername() + ": " + risultati.size() + " video trovati!");
        // 🔔 Qui possiamo implementare email, Telegram bot, o notifiche push
    }

    // 🔹 Aggiorna il monitoraggio quando un utente cambia la frequenza
    public void updateUserMonitoring(User user) {
        // 🔹 Cancella il vecchio task
        if (scheduledTasks.containsKey(user.getId())) {
            scheduledTasks.get(user.getId()).cancel(false);
        }
        // 🔹 Pianifica un nuovo task con la nuova frequenza
        scheduleUserMonitoring(user);
    }
}
