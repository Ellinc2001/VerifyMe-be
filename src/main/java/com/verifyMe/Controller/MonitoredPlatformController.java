package com.verifyMe.Controller;

import com.verifyMe.Entity.MonitoredPlatform;
import com.verifyMe.Utils.JwtUtil;
import com.verifyMe.service.MonitoredPlatformServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitored-platforms")
public class MonitoredPlatformController {

    @Autowired
    private MonitoredPlatformServiceI psi;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get")
    public ResponseEntity<List<MonitoredPlatform>> getPlatforms(@RequestHeader("Authorization") String jwtToken) throws Exception {
        if (jwtToken.startsWith("Bearer ")) {
        	jwtToken = jwtToken.substring(7);
        }
        
        String username = jwtUtil.extractUsername(jwtToken);
        return ResponseEntity.ok(psi.getPlatformsByUser(username));
    }

    @PostMapping("/add")
    public ResponseEntity<MonitoredPlatform> addPlatform(@RequestParam String platformName, @RequestParam Long userId) {
        return ResponseEntity.ok(psi.addPlatform(platformName, userId));
    }

    @DeleteMapping("/remove/{platformId}")
    public ResponseEntity<Void> removePlatform(@PathVariable Long platformId) {
    	psi.removePlatform(platformId);
        return ResponseEntity.noContent().build();
    }
}
