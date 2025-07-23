package com.gui.app.util;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUploader {
    public static boolean uploadChunk(File chunkFile, String targetUrl, String fileName, int part, int totalParts, String username) {
        try {
           System.out.println("Started uploadChunk() method with targetUrl : " + targetUrl);
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-File-Name", fileName);
            conn.setRequestProperty("X-Chunk-Number", String.valueOf(part));
            conn.setRequestProperty("X-Total-Chunks", String.valueOf(totalParts));
            conn.setRequestProperty("X-Username", username);

            try (OutputStream out = conn.getOutputStream();
                 FileInputStream fis = new FileInputStream(chunkFile)) {

                byte[] buffer = new byte[8192];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Ended uploadChunk() method responseCode : " + responseCode);
            return responseCode == 200;

        } catch (IOException e) {
            System.out.println("Ended uploadChunk() method with error : " +e.getMessage());
            System.err.println("Chunk upload failed: " + e.getMessage());
            return false;
        }
    }
    public static boolean uploadBytes(byte[] data, String targetUrl, String fileName, int part, int totalParts, String username) {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-File-Name", fileName);
            conn.setRequestProperty("X-Chunk-Number", String.valueOf(part));
            conn.setRequestProperty("X-Total-Chunks", String.valueOf(totalParts));
            conn.setRequestProperty("X-Username", username);

            try (OutputStream out = conn.getOutputStream()) {
                out.write(data);
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
