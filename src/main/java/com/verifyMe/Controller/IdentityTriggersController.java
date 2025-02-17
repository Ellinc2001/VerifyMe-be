package com.verifyMe.Controller;

import com.verifyMe.Entity.IdentityTriggers;
import com.verifyMe.service.IdentityTriggersService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identity-triggers")
public class IdentityTriggersController {

    private final IdentityTriggersService triggersService;

    public IdentityTriggersController(IdentityTriggersService triggersService) {
        this.triggersService = triggersService;
    }

    // 🔹 Ottieni tutti gli Identity Triggers di un utente
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IdentityTriggers>> getTriggersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(triggersService.getTriggersByUserId(userId));
    }

    // 🔹 Crea un nuovo Identity Trigger per un utente
    @PostMapping("/create/{userId}")
    public ResponseEntity<IdentityTriggers> createTrigger(@PathVariable Long userId, @RequestBody IdentityTriggers trigger) {
        return ResponseEntity.ok(triggersService.createTrigger(userId, trigger));
    }

    // 🔹 Aggiorna un Identity Trigger
    @PutMapping("/{triggerId}")
    public ResponseEntity<IdentityTriggers> updateTrigger(@PathVariable Long triggerId, @RequestBody IdentityTriggers updatedTrigger) {
        return ResponseEntity.ok(triggersService.updateTrigger(triggerId, updatedTrigger));
    }

    // 🔹 Elimina un Identity Trigger
    @DeleteMapping("/{triggerId}")
    public ResponseEntity<Void> deleteTrigger(@PathVariable Long triggerId) {
        triggersService.deleteTrigger(triggerId);
        return ResponseEntity.noContent().build();
    }
}
