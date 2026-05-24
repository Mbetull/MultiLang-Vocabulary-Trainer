package com.betul.view;

import com.betul.dao.UserDAO;
import com.betul.service.AppSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class ProfileView extends VBox {
    private final UserDAO userDAO = new UserDAO();

    public ProfileView() {
        this.setSpacing(24);
        this.setMaxWidth(800);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_LEFT);

        VBox titleBox = new VBox(4);
        Label titleLabel = new Label("Hesap & Sistem Ayarları");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label subtitleLabel = new Label("Manage your academic credentials, interface localization, and dynamic target languages.");
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        this.getChildren().add(titleBox);

        String fullName = AppSettings.getInstance().getCurrentUser() != null ? AppSettings.getInstance().getCurrentUser().getUsername() : "User";
        String email = AppSettings.getInstance().getCurrentUser() != null ? AppSettings.getInstance().getCurrentUser().getEmail() : "N/A";
        int userId = AppSettings.getInstance().getCurrentUser() != null ? AppSettings.getInstance().getCurrentUser().getId() : 0;

        boolean isPremiumNow = AppSettings.getInstance().isCurrentUserPremium();
        String rankStatus = isPremiumNow ? "PRO MEMBER ✨" : "STANDARD MEMBER 🛡️";
        String rankColor = isPremiumNow ? "#00e676" : "#64748b";

        HBox profileCard = new HBox(20);
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 24; -fx-border-color: #e4e7eb; -fx-border-radius: 16;");

        String initials = fullName.length() >= 2 ? fullName.substring(0, 2).toUpperCase() : "U";
        Label avatarLabel = new Label(initials);
        avatarLabel.setAlignment(Pos.CENTER);
        avatarLabel.setPrefSize(70, 70);
        avatarLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: linear-gradient(to bottom right, #1a237e, #0d47a1); -fx-text-fill: white; -fx-background-radius: 50;");

        VBox userDetails = new VBox(6);
        Label lblName = new Label(fullName);
        lblName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label lblEmail = new Label("✉️  " + email);
        lblEmail.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        Label lblRankBadge = new Label(rankStatus);
        lblRankBadge.setStyle("-fx-background-color: " + rankColor + "15; -fx-text-fill: " + rankColor + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 8 4 8; -fx-background-radius: 6;");

        userDetails.getChildren().addAll(lblName, lblEmail, lblRankBadge);
        profileCard.getChildren().addAll(avatarLabel, userDetails);
        this.getChildren().add(profileCard);

        VBox languageCard = new VBox(16);
        languageCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 24; -fx-border-color: #e4e7eb; -fx-border-radius: 16;");

        Label langTitle = new Label("🌐 Çoklu Dil Kütüphanesi & Lokalizasyon");
        langTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");

        VBox appLangBox = new VBox(6);
        Label lblAppLang = new Label("Uygulama Arayüz Dili (UI Language)");
        lblAppLang.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");
        ComboBox<String> comboAppLang = new ComboBox<>();
        comboAppLang.getItems().addAll("Türkçe (TR)", "English (US)");
        comboAppLang.setValue("Türkçe (TR)");
        comboAppLang.setMaxWidth(Double.MAX_VALUE);
        comboAppLang.setStyle("-fx-background-radius: 8;");
        appLangBox.getChildren().addAll(lblAppLang, comboAppLang);

        VBox multiLangBox = new VBox(8);
        Label lblMultiLang = new Label("Kelimelerim Sayfasında Aktif Olacak Dil Odaları");
        lblMultiLang.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");

        CheckBox chkEnglish = new CheckBox("İngilizce (English) Kütüphanesi");
        CheckBox chkFrench = new CheckBox("Fransızca (Français) Kütüphanesi");
        CheckBox chkGerman = new CheckBox("Almanca (Deutsch) Kütüphanesi");
        CheckBox chkSpanish = new CheckBox("İspanyolca (Español) Kütüphanesi");

        List<String> currentActiveLangs = AppSettings.getInstance().getActiveLanguages();
        if (currentActiveLangs == null || currentActiveLangs.isEmpty()) {
            currentActiveLangs = new ArrayList<>();
            currentActiveLangs.add("İngilizce");
        }
        if (currentActiveLangs.contains("İngilizce")) chkEnglish.setSelected(true);
        if (currentActiveLangs.contains("Fransızca")) chkFrench.setSelected(true);
        if (currentActiveLangs.contains("Almanca")) chkGerman.setSelected(true);
        if (currentActiveLangs.contains("İspanyolca")) chkSpanish.setSelected(true);

        multiLangBox.getChildren().addAll(lblMultiLang, chkEnglish, chkFrench, chkGerman, chkSpanish);

        Button btnSaveSettings = new Button("Sistem & Kütüphane Ayarlarını Güncelle");
        btnSaveSettings.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
        btnSaveSettings.setMaxWidth(Double.MAX_VALUE);

        btnSaveSettings.setOnAction(e -> {
            List<String> selectedLangs = new ArrayList<>();

            if (chkEnglish.isSelected()) selectedLangs.add("İngilizce");
            if (chkFrench.isSelected()) selectedLangs.add("Fransızca");
            if (chkGerman.isSelected()) selectedLangs.add("Almanca");
            if (chkSpanish.isSelected()) selectedLangs.add("İspanyolca");

            if (selectedLangs.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Lütfen en az bir aktif dil odası seçin!").showAndWait();
                return;
            }

            AppSettings.getInstance().setActiveLanguages(selectedLangs);

            new Alert(Alert.AlertType.INFORMATION, "Sistem konfigürasyonu başarıyla güncellendi!\nArayüz Dili: " + comboAppLang.getValue() + "\nAktif Dil Odaları: " + selectedLangs).showAndWait();

            if (MainLayout.getInstance() != null) {
                MainLayout.getInstance().setContent(new ProfileView());
            }
        });

        languageCard.getChildren().addAll(langTitle, appLangBox, multiLangBox, btnSaveSettings);
        this.getChildren().addAll(languageCard);

        VBox activationCard = new VBox(16);
        if (isPremiumNow) {
            activationCard.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 16; -fx-padding: 24; -fx-border-color: #c8e6c9; -fx-border-radius: 16;");
            Label secureTitle = new Label("🛡️ Secure Access Level Authorized");
            secureTitle.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 16px; -fx-font-weight: bold;");
            Label secureDesc = new Label("Your Betulingo Pro licence is active for the 2026 academic calendar. Quota thresholds and data-stream limits have been lifted permanently.");
            secureDesc.setStyle("-fx-text-fill: #4caf50; -fx-font-size: 13px;");
            secureDesc.setWrapText(true);
            activationCard.getChildren().addAll(secureTitle, secureDesc);
        } else {
            activationCard.setStyle("-fx-background-color: linear-gradient(to right, #1a237e, #0d47a1); -fx-background-radius: 16; -fx-padding: 24;");
            Label promoTitle = new Label("Unlock Betulingo Pro ✨");
            promoTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            Label promoDesc = new Label("Enter your cyber activation token to lift the daily 5-word quota restriction.");
            promoDesc.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
            TextField txtCode = new TextField();
            txtCode.setPromptText("Enter Activation Code (e.g., BETULINGO_PRO_2026_NX7)");
            txtCode.setStyle("-fx-background-radius: 8; -fx-padding: 10;");
            Button btnUpgrade = new Button("Upgrade to Premium Ranks");
            btnUpgrade.setStyle("-fx-background-color: #00e676; -fx-text-fill: #1a237e; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
            btnUpgrade.setMaxWidth(Double.MAX_VALUE);
            activationCard.getChildren().addAll(promoTitle, promoDesc, txtCode, btnUpgrade);

            btnUpgrade.setOnAction(e -> {
                String code = txtCode.getText().trim();
                if (code.isEmpty()) return;
                if (userDAO.activatePremium(userId, code)) {
                    AppSettings.getInstance().getCurrentUser().setIsPremium(1);
                    new Alert(Alert.AlertType.INFORMATION, "Account elevated to Premium!").showAndWait();
                    if (MainLayout.getInstance() != null) MainLayout.getInstance().setContent(new ProfileView());
                } else {
                    new Alert(Alert.AlertType.ERROR, "Invalid token!").showAndWait();
                }
            });
        }
        this.getChildren().add(activationCard);
    }
}