package com.betul.dao;

import com.betul.database.DatabaseConfig;
import com.betul.model.User;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Şifre hash'leme sırasında kritik hata!", e);
        }
    }

    public boolean registerUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password, is_premium) VALUES (?, ?, ?, 0)";

        try (java.sql.Connection conn = com.betul.database.DatabaseConfig.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username); // Artık buraya sistem ad-soyadı basacak
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Kullanıcı kayıt hatası: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, hashPassword(password)); // Girilen şifreyi hash'leyip db ile karşılaştırıyoruz

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getInt("is_premium"),
                            rs.getString("app_language"),
                            rs.getString("learning_language")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Giriş hatası: " + e.getMessage());
        }
        return null;
    }

    public boolean activatePremium(int userId, String tokenCode) {
        String checkTokenSql = "SELECT is_used FROM activation_tokens WHERE token_code = ?";
        String useTokenSql = "UPDATE activation_tokens SET is_used = 1 WHERE token_code = ?";
        String upgradeUserSql = "UPDATE users SET is_premium = 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(checkTokenSql)) {
                pstmt.setString(1, tokenCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next() || rs.getInt("is_used") == 1) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(useTokenSql)) {
                pstmt.setString(1, tokenCode);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(upgradeUserSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            System.err.println("Token aktivasyon hatası: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}