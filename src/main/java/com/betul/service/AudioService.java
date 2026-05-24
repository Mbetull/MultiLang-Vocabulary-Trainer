package com.betul.service;

public class AudioService {

    public static void playEnglishSound(String text) {
        new Thread(() -> {
            try {
                String sanitizedText = text.replaceAll("[^a-zA-Z0-9 ]", "");

                String command = "Add-Type -AssemblyName System.Speech; " +
                        "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                        "$speak.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::Neutral, [System.Speech.Synthesis.VoiceAge]::Adult, 0, [System.Globalization.CultureInfo]::GetCultureInfo('en-US')); " +
                        "$speak.Speak('" + sanitizedText + "')";

                ProcessBuilder pb = new ProcessBuilder("powershell", "-Command", command);
                Process process = pb.start();
                process.waitFor(); // İşlemin tamamlanmasını arka planda bekle

                System.out.println("-> Windows Native TTS successfully spoken: " + sanitizedText);
            } catch (Exception e) {
                System.err.println("Yerel Windows ses motoru hatası: " + e.getMessage());
            }
        }).start();
    }
}