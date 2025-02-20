package com.verifyMe.Controller;

import com.verifyMe.Entity.IdentityTriggers;
import com.verifyMe.service.IdentityTriggerServiceI;
import com.verifyMe.service.IdentityTriggersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identity-triggers")
public class IdentityTriggersController {

	@Autowired
    private IdentityTriggerServiceI tgsi;

    // 🔹 Ottieni tutti gli Identity Triggers di un utente
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IdentityTriggers>> getTriggersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(tgsi.getTriggersByUserId(userId));
    }

    // 🔹 Crea un nuovo Identity Trigger per un utente
    @PostMapping("/create/{userId}")
    public ResponseEntity<IdentityTriggers> createTrigger(@PathVariable Long userId, @RequestBody IdentityTriggers trigger) {
        return ResponseEntity.ok(tgsi.createTrigger(userId, trigger));
    }

    // 🔹 Aggiorna un Identity Trigger
    @PutMapping("/{triggerId}")
    public ResponseEntity<IdentityTriggers> updateTrigger(@PathVariable Long triggerId, @RequestBody IdentityTriggers updatedTrigger) {
        return ResponseEntity.ok(tgsi.updateTrigger(triggerId, updatedTrigger));
    }

    // 🔹 Elimina un Identity Trigger
    @DeleteMapping("/{triggerId}")
    public ResponseEntity<Void> deleteTrigger(@PathVariable Long triggerId) {
    	tgsi.deleteTrigger(triggerId);
        return ResponseEntity.noContent().build();
    }
}
