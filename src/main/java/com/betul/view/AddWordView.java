package com.betul.view;

import com.betul.dao.WordDAO;
import com.betul.service.AppSettings;
import com.betul.service.TranslationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class AddWordView extends VBox {
    private final WordDAO wordDAO = new WordDAO();

    public AddWordView() {
        this.setSpacing(20);
        this.setPadding(new Insets(24));
        this.setMaxWidth(600);
        this.setAlignment(Pos.TOP_LEFT);

        VBox titleBox = new VBox(4);
        Label titleLabel = new Label("Yeni Kelime Tanımlama Paneli");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label subtitleLabel = new Label("Kelimelerinizi Enter tuşuyla kesintisiz şekilde kütüphanenize işleyin.");
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        this.getChildren().add(titleBox);

        VBox formCard = new VBox(16);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 24; -fx-border-color: #e4e7eb; -fx-border-radius: 12;");

        VBox wordBox = new VBox(6);
        Label lblWord = new Label("Yabancı Kelime / İfade (Word Text)");
        lblWord.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        TextField txtWord = new TextField();
        txtWord.setPromptText("Örn: Concurrent, Malware, Chignon");
        txtWord.setStyle("-fx-background-radius: 8; -fx-padding: 10;");
        wordBox.getChildren().addAll(lblWord, txtWord);

        VBox transBox = new VBox(6);
        Label lblTrans = new Label("Türkçe Karşılığı (Meaning - Seçilen Dile Göre Canlı Çeviri ✨)");
        lblTrans.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        TextField txtTranslation = new TextField();
        txtTranslation.setPromptText("Yapay zeka otomatik dolduracak...");
        txtTranslation.setStyle("-fx-background-radius: 8; -fx-padding: 10;");
        transBox.getChildren().addAll(lblTrans, txtTranslation);

        VBox langBox = new VBox(6);
        Label lblLang = new Label("Eklenecek Hedef Dil Odası");
        lblLang.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        ComboBox<String> comboLang = new ComboBox<>();

        List<String> activeLangs = AppSettings.getInstance().getActiveLanguages();
        if (activeLangs != null && !activeLangs.isEmpty()) {
            comboLang.getItems().addAll(activeLangs);
            comboLang.setValue(activeLangs.get(0));
        } else {
            comboLang.getItems().add("İngilizce");
            comboLang.setValue("İngilizce");
        }
        comboLang.setMaxWidth(Double.MAX_VALUE);
        comboLang.setStyle("-fx-background-radius: 8;");
        langBox.getChildren().addAll(lblLang, comboLang);

        Button btnCancel = new Button("İptal / Kütüphaneye Dön");
        btnCancel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

        Button btnSave = new Button("🚀 Kelimeyi Kaydet");
        btnSave.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

        HBox buttonRow = new HBox(12, btnCancel, btnSave);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        formCard.getChildren().addAll(wordBox, transBox, langBox, buttonRow);
        this.getChildren().add(formCard);

        txtWord.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                triggerAutoTranslation(txtWord, txtTranslation, comboLang);
            }
        });

        Runnable saveAction = () -> {
            String wordText = txtWord.getText().trim();
            String transText = txtTranslation.getText().trim();
            String selectedRoom = comboLang.getValue();

            if (wordText.isEmpty() || transText.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Lütfen kelime ve anlam alanlarını boş bırakmayın!").showAndWait();
                return;
            }

            if (AppSettings.getInstance().getCurrentUser() == null) return;
            int userId = AppSettings.getInstance().getCurrentUser().getId();
            boolean isPremium = AppSettings.getInstance().isCurrentUserPremium();

            if (wordDAO.addWord(userId, isPremium, wordText, transText, 0, selectedRoom)) {
                txtWord.clear();
                txtTranslation.clear();
                txtWord.requestFocus();
                System.out.println("-> [Betulingo] Kelime başarıyla dil odasına şifrelendi: " + wordText);
            } else {
                new Alert(Alert.AlertType.ERROR, "Mükerrer kayıt veya kota kısıt engeli!").showAndWait();
            }
        };

        btnSave.setOnAction(e -> saveAction.run());
        btnCancel.setOnAction(e -> {
            if (MainLayout.getInstance() != null) {
                MainLayout.getInstance().setContent(new VocabularyView());
            }
        });

        txtWord.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String word = txtWord.getText().trim();
                if (!word.isEmpty()) {
                    triggerAutoTranslation(txtWord, txtTranslation, comboLang);
                    txtTranslation.requestFocus();
                }
            }
        });

        txtTranslation.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                comboLang.requestFocus();
            }
        });

        comboLang.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveAction.run();
            }
        });
    }

    private void triggerAutoTranslation(TextField txtWord, TextField txtTranslation, ComboBox<String> comboLang) {
        String word = txtWord.getText().trim();
        String selectedLanguageRoom = comboLang.getValue(); // "Fransızca", "Almanca" vb. tam string değer alınır

        if (word.length() >= 2 && selectedLanguageRoom != null) {
            txtTranslation.setPromptText("⌛ Yapay zeka [" + selectedLanguageRoom + "] odasına göre çeviriyor...");
            new Thread(() -> {
                // API'ye bizzat seçilen dil parametresini zorluyoruz!
                String aiTranslation = TranslationService.translateToTurkish(word, selectedLanguageRoom);
                javafx.application.Platform.runLater(() -> {
                    if (txtWord.getText().trim().equalsIgnoreCase(word)) {
                        txtTranslation.setText(aiTranslation);
                    }
                });
            }).start();
        }
    }
}