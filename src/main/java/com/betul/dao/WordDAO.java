package com.betul.dao;

import com.betul.database.DatabaseConfig;
import com.betul.model.Word;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WordDAO {

    public int getWordsAddedToday(int userId) {
        String sql = "SELECT COUNT(*) FROM words WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Günlük kelime sayım hatası: " + e.getMessage());
        }
        return 0;
    }

    public boolean addWord(int userId, boolean isPremium, String word, String translation, int difficulty, String category) {

        String checkSql = "SELECT COUNT(*) FROM words WHERE user_id = ? AND LOWER(word_text) = LOWER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {

            checkPstmt.setInt(1, userId);
            checkPstmt.setString(2, word.trim());

            try (ResultSet rs = checkPstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("-> [Betulingo] Engellendi: Bu kelime zaten kütüphanenizde mevcut! (Duplicate Blocked)");
                    return false; // Kelime zaten var, ekleme yapmadan doğrudan false dönüyoruz!
                }
            }
        } catch (Exception e) {
            System.err.println("Mükerrer kelime kontrol hatası: " + e.getMessage());
        }

        if (!isPremium) {
            int todayCount = getWordsAddedToday(userId);
            if (todayCount >= 5) {
                System.out.println("-> [Betulingo] Kısıt Sınırı: Standart kullanıcılar günde en fazla 5 kelime ekleyebilir!");
                return false;
            }
        }

        String sql = "INSERT INTO words (user_id, word_text, translation_text, difficulty, category, mastery) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, word.trim());
            pstmt.setString(3, translation.trim());
            pstmt.setInt(4, difficulty);
            pstmt.setString(5, category);
            pstmt.setInt(6, 0);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Kelime ekleme hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMastery(int wordId, int increment) {
        String sql = "UPDATE words SET mastery = MIN(100, mastery + ?) WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, increment);
            pstmt.setInt(2, wordId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Mastery güncelleme hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWord(int wordId) {
        String sql = "DELETE FROM words WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, wordId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Kelime silme hatası: " + e.getMessage());
            return false;
        }
    }

    public List<Word> getWordsByUserId(int userId) {
        List<Word> wordList = new ArrayList<>();
        String sql = "SELECT * FROM words WHERE user_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Word word = new Word(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("word_text"),
                            rs.getString("translation_text"),
                            rs.getInt("difficulty"),
                            rs.getInt("mastery"),
                            rs.getString("category")
                    );
                    wordList.add(word);
                }
            }
        } catch (Exception e) {
            System.err.println("Kelime listeleme hatası: " + e.getMessage());
        }
        return wordList;
    }


    public int getTotalWordCount(int userId) {
        String sql = "SELECT COUNT(*) FROM words WHERE user_id = ?";
        try (java.sql.Connection conn = com.betul.database.DatabaseConfig.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Total word count hatası: " + e.getMessage());
        }
        return 0;
    }

    public int getMasteredWordCount(int userId) {
        String sql = "SELECT COUNT(*) FROM words WHERE user_id = ? AND mastery >= 70";
        try (java.sql.Connection conn = com.betul.database.DatabaseConfig.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Mastered count hatası: " + e.getMessage());
        }
        return 0;
    }

    public int getInProgressWordCount(int userId) {
        String sql = "SELECT COUNT(*) FROM words WHERE user_id = ? AND mastery < 70";
        try (java.sql.Connection conn = com.betul.database.DatabaseConfig.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("In progress count hatası: " + e.getMessage());
        }
        return 0;
    }

    public boolean updateWordMastery(int wordId, int newMastery) {
        String sql = "UPDATE words SET mastery = ? WHERE id = ?";
        try (java.sql.Connection conn = com.betul.database.DatabaseConfig.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newMastery);
            pstmt.setInt(2, wordId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Veritabanı skor güncelleme hatası: " + e.getMessage());
            return false;
        }
    }


    public List<Word> getWordsByLanguage(int userId, String langCode) {
        List<Word> list = new ArrayList<>();
        String sql = "SELECT * FROM words WHERE user_id = ? AND category = ?";
        try (java.sql.Connection conn = com.betul.database.DatabaseConfig.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, langCode); // Örn: "İngilizce", "Fransızca"

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Word w = new Word();
                    w.setId(rs.getInt("id"));
                    w.setUserId(rs.getInt("user_id"));
                    w.setWordText(rs.getString("word_text"));
                    w.setTranslationText(rs.getString("translation_text"));
                    w.setCategory(rs.getString("category")); // Dil bilgisi burada saklanıyor
                    list.add(w);
                }
            }
        } catch (Exception e) {
            System.err.println("Dile göre kelime çekme hatası: " + e.getMessage());
        }
        return list;
    }

}