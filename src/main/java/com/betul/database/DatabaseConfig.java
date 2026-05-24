package com.betul.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Base64;

public class DatabaseConfig {
    private static final String URL = "jdbc:sqlite:vocab.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "email TEXT NOT NULL UNIQUE," +
                    "password_hash TEXT NOT NULL," +
                    "is_premium INTEGER DEFAULT 0," +
                    "app_language TEXT DEFAULT 'TR'," +
                    "learning_language TEXT DEFAULT 'EN'" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS words (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "word_text TEXT NOT NULL," +
                    "translation_text TEXT NOT NULL," +
                    "source_lang TEXT DEFAULT 'EN'," +
                    "target_lang TEXT DEFAULT 'TR'," +
                    "difficulty INTEGER DEFAULT 1," +
                    "mastery INTEGER DEFAULT 0," +
                    "category TEXT DEFAULT 'Genel'," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS activation_tokens (" +
                    "token_code TEXT PRIMARY KEY," +
                    "is_used INTEGER DEFAULT 0" + // 0: Kullanılmamış, 1: Kullanılmış
                    ");");

            String token1 = new String(Base64.getDecoder().decode("QkVUVUxJTkdPX1BST18yMDI2X05YNw=="));
            stmt.execute("INSERT OR IGNORE INTO activation_tokens (token_code, is_used) VALUES ('" + token1 + "', 0);");

            String token2 = new String(Base64.getDecoder().decode("Q1lCRVJfVklQX0FDQ0VTU185OVg="));
            stmt.execute("INSERT OR IGNORE INTO activation_tokens (token_code, is_used) VALUES ('" + token2 + "', 0);");

            System.out.println("-> [Betulingo] Veritabanı mimarisi ve güvenli tokenlar başarıyla hazırlandı!");

        } catch (Exception e) {
            System.err.println("Veritabanı kurulum hatası: " + e.getMessage());
        }
    }

}