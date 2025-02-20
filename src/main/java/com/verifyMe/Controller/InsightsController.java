package com.verifyMe.Controller;

import com.verifyMe.service.InsightsService;
import com.verifyMe.service.InsightsServiceI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/insights")
public class InsightsController {

    @Autowired
    private InsightsServiceI insi;

    // 📌 Endpoint per ottenere le insights tramite Username
    @GetMapping("/findByUsername/{username}")
    public ResponseEntity<?> getInsightsByUsername(@PathVariable String username) {
        Map<String, Map<String, Integer>> insights = insi.getInsightsForUser(username);
        return ResponseEntity.ok().body(insights);
    }
}
