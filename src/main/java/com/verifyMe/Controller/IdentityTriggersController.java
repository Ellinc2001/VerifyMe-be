package com.verifyMe.Controller;

import com.verifyMe.Entity.IdentityTriggers;
import com.verifyMe.Utils.JwtUtil;
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
	
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get-all")
    public ResponseEntity<List<IdentityTriggers>> getTriggersByUser(@RequestHeader("Authorization") String jwtToken) throws Exception {
        if (jwtToken.startsWith("Bearer ")) {
        	jwtToken = jwtToken.substring(7);
        }
        
        String username = jwtUtil.extractUsername(jwtToken);
        return ResponseEntity.ok(tgsi.getTriggersByUserId(username));
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<IdentityTriggers> createTrigger(@PathVariable Long userId, @RequestBody IdentityTriggers trigger) {
        return ResponseEntity.ok(tgsi.createTrigger(userId, trigger));
    }

    @PutMapping("/{triggerId}")
    public ResponseEntity<IdentityTriggers> updateTrigger(@PathVariable Long triggerId, @RequestBody IdentityTriggers updatedTrigger) {
        return ResponseEntity.ok(tgsi.updateTrigger(triggerId, updatedTrigger));
    }

    @DeleteMapping("/{triggerId}")
    public ResponseEntity<Void> deleteTrigger(@PathVariable Long triggerId) {
    	tgsi.deleteTrigger(triggerId);
        return ResponseEntity.noContent().build();
    }
}
