package com.verifyMe.Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class FaceEncryptionUtil {

    private static final String ALGORITHM = "AES";

    // 🔹 Genera una chiave AES (puoi salvarla in modo sicuro per riutilizzarla)
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256, new SecureRandom()); // 256-bit key
        return keyGenerator.generateKey();
    }

    // 🔹 Crittografa i dati biometrici (byte array)
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // 🔹 Decrittografa i dati biometrici
    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    // 🔹 Converte la chiave AES in una stringa Base64 per salvarla nel database
    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // 🔹 Converte una stringa Base64 in una chiave AES
    public static SecretKey decodeKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
}

