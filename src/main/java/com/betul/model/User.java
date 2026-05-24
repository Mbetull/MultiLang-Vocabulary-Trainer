package com.betul.model;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private int isPremium;
    private String appLanguage;
    private String learningLanguage;

    public User() {}

    public User(int id, String username, String email, String passwordHash, int isPremium, String appLanguage, String learningLanguage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isPremium = isPremium;
        this.appLanguage = appLanguage;
        this.learningLanguage = learningLanguage;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getIsPremium() { return isPremium; }
    public void setIsPremium(int isPremium) { this.isPremium = isPremium; }

    public String getAppLanguage() { return appLanguage; }
    public void setAppLanguage(String appLanguage) { this.appLanguage = appLanguage; }

    public String getLearningLanguage() { return learningLanguage; }
    public void setLearningLanguage(String learningLanguage) { this.learningLanguage = learningLanguage; }

    public boolean isPremiumUser() {
        return this.isPremium == 1;
    }
}