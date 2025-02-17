package com.verifyMe.Controller;

import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;


import com.verifyMe.Entity.User;
import com.verifyMe.Repository.UserRepository;
import com.verifyMe.Utils.FaceEmbeddingUtil;
import com.verifyMe.Utils.FaceEncryptionUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository usr;
	
    private final SecretKey secretKey; // 🔹 Chiave AES per la crittografia

    public UserController(UserRepository userRepository) throws Exception {
        this.secretKey = FaceEncryptionUtil.generateKey(); // 🔹 Genera una chiave AES per la crittografia
    }
	
    @PostMapping("/create")
    public ResponseEntity<String> setFaceEmbedding(@RequestBody float[] embedding, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> optionalUser = usr.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        User user = optionalUser.get();

        try {
            byte[] encryptedEmbedding = FaceEncryptionUtil.encrypt(FaceEmbeddingUtil.floatArrayToByteArray(embedding), secretKey);
            user.setEncryptedFaceEmbedding(encryptedEmbedding); // ✅ Ora il metodo esiste!
            usr.save(user);
            return ResponseEntity.ok("Face embedding salvato con successo!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore durante la crittografia: " + e.getMessage());
        }
    }

}
