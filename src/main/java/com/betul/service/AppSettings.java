package com.betul.service;

import com.betul.model.User;
import java.util.Locale;
import java.util.ResourceBundle;

public class AppSettings {
    private static AppSettings instance;
    private Locale currentLocale;
    private ResourceBundle bundle;
    private User currentUser;

    private java.util.List<String> activeLanguages = new java.util.ArrayList<>(java.util.Arrays.asList("İngilizce"));

    public java.util.List<String> getActiveLanguages() {
        return activeLanguages;
    }

    public void setActiveLanguages(java.util.List<String> languages) {
        this.activeLanguages = languages;
    }

    private AppSettings() {
        setAppLanguage("TR");
    }

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public void setAppLanguage(String langCode) {
        currentLocale = new Locale(langCode.toLowerCase());
        bundle = ResourceBundle.getBundle("messages", currentLocale);

        if (currentUser != null) {
            currentUser.setAppLanguage(langCode.toUpperCase());
        }
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            setAppLanguage(user.getAppLanguage());
        }
    }

    public boolean isCurrentUserPremium() {
        return currentUser != null && currentUser.isPremiumUser();
    }
}