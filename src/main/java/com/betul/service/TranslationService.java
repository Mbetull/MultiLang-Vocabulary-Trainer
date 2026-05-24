package com.betul.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationService {

    public static String translateToTurkish(String text) {
        return translateToTurkish(text, "İngilizce");
    }

    public static String translateToTurkish(String text, String fromLanguage) {
        if (text == null || text.trim().isEmpty()) return "";

        String sourceLang = "en";
        String langLower = fromLanguage.toLowerCase();

        if (langLower.contains("fransızca") || langLower.contains("french") || langLower.contains("français")) {
            sourceLang = "fr";
        } else if (langLower.contains("almanca") || langLower.contains("german") || langLower.contains("deutsch")) {
            sourceLang = "de";
        } else if (langLower.contains("ispanyolca") || langLower.contains("spanish") || langLower.contains("español")) {
            sourceLang = "es";
        }

        try {
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
                    + sourceLang + "&tl=tr&dt=t&q=" + URLEncoder.encode(text.trim(), StandardCharsets.UTF_8);

            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            String json = response.toString();
            if (json.contains("[[\"")) {
                String result = json.substring(json.indexOf("[[\"") + 3);
                result = result.substring(0, result.indexOf("\""));
                return result;
            }

            return text;
        } catch (Exception e) {
            System.err.println("-> [Betulingo AI] Çok dilli çeviri hatası: " + e.getMessage());
            return "[Çeviri Hatası]";
        }
    }
}