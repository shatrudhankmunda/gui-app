package com.gui.app.util;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileUtils {
    private static final int CHUNK_SIZE = 1024 * 1024; // 1 MB
    private static final String AES = "AES";
    private static final String SECRET = "SuperSecureKey123"; // can be user-based or per file

    public static SecretKey generateKey(String passphrase) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(passphrase.getBytes("UTF-8"));
        return new SecretKeySpec(keyBytes, 0, 16, AES); // 128-bit AES
    }

    public static byte[] encryptChunk(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }
    public static byte[] decryptChunk(byte[] encrypted, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(encrypted);
   }
        public static void chunkAndEncryptAndUpload(File inputFile, String username, String loadBalancerUrl) throws Exception {
        SecretKey key = generateKey(SECRET);

        try (FileInputStream fis = new FileInputStream(inputFile)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            int part = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] actualData = (bytesRead == CHUNK_SIZE) ? buffer : copyOf(buffer, bytesRead);
                byte[] encrypted = encryptChunk(actualData, key);

                // upload directly without saving to disk
                boolean uploaded = HttpUploader.uploadBytes(
                    encrypted,
                    loadBalancerUrl,
                    inputFile.getName(),
                    part,
                    -1, // optional total parts
                    username
                );

                if (!uploaded) throw new RuntimeException("Failed to upload part " + part);
                part++;
            }
        }
    }
    private static byte[] copyOf(byte[] source, int length) {
        byte[] dest = new byte[length];
        System.arraycopy(source, 0, dest, 0, length);
        return dest;
    }
}
