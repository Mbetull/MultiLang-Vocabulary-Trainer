package com.betul.view;

import com.betul.dao.WordDAO;
import com.betul.model.Word;
import com.betul.service.AppSettings;
import com.betul.service.AudioService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PracticeView extends VBox {
    private final WordDAO wordDAO = new WordDAO();
    private List<Word> quizWords = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private Word currentWord;
    private String selectedLanguage = "";

    private final Label lblQuestion = new Label();
    private final Button[] btnOptions = new Button[4];
    private final Label lblFeedback = new Label();
    private final ProgressBar progressBar = new ProgressBar(0);
    private final VBox quizCard = new VBox(20);

    private final VBox setupCard = new VBox(16);
    private final ComboBox<String> comboLangSelect = new ComboBox<>();

    public PracticeView() {
        this.setSpacing(24);
        this.setMaxWidth(600);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_LEFT);

        VBox titleBox = new VBox(4);
        Label titleLabel = new Label("🎯 Bilişsel Pratik Alanı");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label subtitleLabel = new Label("İstediğiniz dil odasını seçin. Doğru cevaplar +10, yanlışlar -10 puan yazar.");
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        this.getChildren().add(titleBox);

        setupCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 32; -fx-border-color: #e4e7eb; -fx-border-radius: 16;");
        setupCard.setAlignment(Pos.CENTER);

        Label lblSelectPrompt = new Label("Pratik Yapmak İstediğiniz Dil Odasını Seçin:");
        lblSelectPrompt.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        List<String> activeLangs = AppSettings.getInstance().getActiveLanguages();
        if (activeLangs != null && !activeLangs.isEmpty()) {
            comboLangSelect.getItems().addAll(activeLangs);
            comboLangSelect.setValue(activeLangs.get(0));
        } else {
            comboLangSelect.getItems().add("İngilizce");
            comboLangSelect.setValue("İngilizce");
        }
        comboLangSelect.setPrefWidth(300);
        comboLangSelect.setStyle("-fx-background-radius: 8; -fx-padding: 6;");

        Button btnStartQuiz = new Button("🚀 Pratiği Başlat");
        btnStartQuiz.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;");
        btnStartQuiz.setOnAction(e -> {
            selectedLanguage = comboLangSelect.getValue();
            startPracticeSession();
        });

        setupCard.getChildren().addAll(lblSelectPrompt, comboLangSelect, btnStartQuiz);
        this.getChildren().add(setupCard);

        quizCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 32; -fx-border-color: #e4e7eb; -fx-border-radius: 16;");
        quizCard.setAlignment(Pos.CENTER);

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #24b2fe;");
        lblQuestion.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-padding: 10 0 10 0;");

        Button btnAudio = new Button("🔊 Telaffuz Dinle");
        btnAudio.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #0d47a1; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAudio.setOnAction(e -> { if (currentWord != null) AudioService.playEnglishSound(currentWord.getWordText()); });

        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(12);
        optionsGrid.setVgap(12);
        optionsGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            btnOptions[i] = new Button();
            btnOptions[i].setPrefSize(240, 55);
            btnOptions[i].setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #334155; -fx-cursor: hand;");
            int finalI = i;
            btnOptions[i].setOnAction(e -> checkAnswer(btnOptions[finalI].getText(), btnOptions[finalI]));
            optionsGrid.add(btnOptions[i], i % 2, i / 2);
        }
        lblFeedback.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        quizCard.getChildren().addAll(progressBar, lblQuestion, btnAudio, optionsGrid, lblFeedback);
    }

    private void startPracticeSession() {
        this.getChildren().remove(setupCard);
        if (!this.getChildren().contains(quizCard)) {
            this.getChildren().add(quizCard);
        }

        if (AppSettings.getInstance().getCurrentUser() == null) return;
        int userId = AppSettings.getInstance().getCurrentUser().getId();

        List<String> activeLanguages = AppSettings.getInstance().getActiveLanguages();
        List<Word> allWords = wordDAO.getWordsByUserId(userId);
        List<Word> filteredWords = new ArrayList<>();

        for (Word w : allWords) {
            String cat = w.getCategory();
            if (selectedLanguage.equalsIgnoreCase("İngilizce")) {
                if (cat == null || cat.trim().isEmpty() || cat.equalsIgnoreCase("General") || cat.equalsIgnoreCase("Academic") || cat.equalsIgnoreCase("İngilizce")) {
                    filteredWords.add(w);
                }
            } else {
                if (cat != null && cat.equalsIgnoreCase(selectedLanguage)) {
                    filteredWords.add(w);
                }
            }
        }

        if (filteredWords.size() < 4) {
            quizCard.getChildren().clear();
            Label lblWarn = new Label("🔒 Havuz Yetersiz!\n[" + selectedLanguage + "] odasında pratik yapabilmek için en az 4 adet kelime bulunmalıdır.\nMevcut kelime sayısı: " + filteredWords.size());
            lblWarn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #c62828; -fx-text-alignment: center;");

            Button btnBack = new Button("↩️ Geri Dön");
            btnBack.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
            btnBack.setOnAction(e -> { if (MainLayout.getInstance() != null) MainLayout.getInstance().setContent(new PracticeView()); });

            quizCard.getChildren().addAll(lblWarn, btnBack);
            return;
        }

        quizWords = new ArrayList<>(filteredWords);
        quizWords.sort(Comparator.comparingInt(Word::getMastery));

        currentQuestionIndex = 0;
        showNextQuestion();
    }

    private void showNextQuestion() {
        lblFeedback.setText("");
        if (currentQuestionIndex >= quizWords.size() || currentQuestionIndex >= 10) {
            quizCard.getChildren().clear();
            Label lblDone = new Label("🎉 Harika! [" + selectedLanguage + "] Pratik Seansını Tamamladınız.");
            lblDone.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

            Button btnRestart = new Button("Yeni Pratik Seansı");
            btnRestart.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
            btnRestart.setOnAction(e -> { if (MainLayout.getInstance() != null) MainLayout.getInstance().setContent(new PracticeView()); });
            quizCard.getChildren().addAll(lblDone, btnRestart);
            return;
        }

        double progress = (double) currentQuestionIndex / Math.min(quizWords.size(), 10);
        progressBar.setProgress(progress);

        currentWord = quizWords.get(currentQuestionIndex);
        lblQuestion.setText(" \"" + currentWord.getWordText() + "\" kelimesinin anlamı nedir?");

        List<String> options = new ArrayList<>();
        options.add(currentWord.getTranslationText());

        List<Word> shufflePool = new ArrayList<>(quizWords);
        Collections.shuffle(shufflePool);

        for (Word w : shufflePool) {
            if (options.size() < 4 && !w.getTranslationText().equalsIgnoreCase(currentWord.getTranslationText())) {
                options.add(w.getTranslationText());
            }
        }

        while (options.size() < 4) {
            options.add("Seçenek Hazırlanıyor " + (options.size() + 1));
        }

        Collections.shuffle(options);
        for (int i = 0; i < 4; i++) {
            btnOptions[i].setText(options.get(i));
            btnOptions[i].setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #334155; -fx-cursor: hand;");
            btnOptions[i].setDisable(false);
        }
    }

    private void checkAnswer(String selectedOption, Button selectedBtn) {
        for (Button btn : btnOptions) btn.setDisable(true);

        boolean isCorrect = selectedOption.equalsIgnoreCase(currentWord.getTranslationText());
        int newScore;

        if (isCorrect) {
            selectedBtn.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #2e7d32; -fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-background-radius: 8;");
            lblFeedback.setText("✨ Doğru Cevap! +10 Puan.");
            lblFeedback.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            newScore = currentWord.getMastery() + 10;
        } else {
            selectedBtn.setStyle("-fx-background-color: #ffebee; -fx-border-color: #c62828; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-background-radius: 8;");
            lblFeedback.setText("❌ Yanlış Cevap! Doğrusu: " + currentWord.getTranslationText());
            lblFeedback.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            newScore = currentWord.getMastery() - 10;
        }

        currentWord.setMastery(newScore);
        wordDAO.updateWordMastery(currentWord.getId(), newScore);

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                currentQuestionIndex++;
                showNextQuestion();
            });
        }).start();
    }
}