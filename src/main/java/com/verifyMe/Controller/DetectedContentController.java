package com.verifyMe.Controller;

import com.verifyMe.DTO.DetectedContentResponseDTO;
import com.verifyMe.Entity.DetectedContent;
import com.verifyMe.Utils.JwtUtil;
import com.verifyMe.service.DetectedContentServiceI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/detected-content")
public class DetectedContentController {

    @Autowired
    private DetectedContentServiceI dcsi;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<DetectedContent> getAllDetectedContent() {
        return dcsi.getAllDetectedContent();
    }
    
    @GetMapping(value = "/get-contents-by-username")
    public ResponseEntity<Object> getContentsByUsername(@RequestHeader("Authorization") String jwtToken){
        if (jwtToken.startsWith("Bearer ")) {
        	jwtToken = jwtToken.substring(7);
        }
        
        String username = jwtUtil.extractUsername(jwtToken);
        DetectedContentResponseDTO contents = this.dcsi.getContentsByUsername(username);
        return ResponseEntity.ok(contents);     
     }

    @GetMapping("/{id}")
    public Optional<DetectedContent> getDetectedContentById(@PathVariable Long id) {
        return dcsi.getDetectedContentById(id);
    }

    @PostMapping
    public DetectedContent saveDetectedContent(@RequestBody DetectedContent detectedContent) {
        return dcsi.saveDetectedContent(detectedContent);
    }

    @DeleteMapping("/{id}")
    public void deleteDetectedContent(@PathVariable Long id) {
    	dcsi.deleteDetectedContent(id);
    }
}
