package com.betul.view;

import com.betul.dao.WordDAO;
import com.betul.model.Word;
import com.betul.service.AppSettings;
import com.betul.service.AudioService;
import com.betul.service.TranslationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class VocabularyView extends VBox {
    private final WordDAO wordDAO = new WordDAO();
    private final TabPane tabPane = new TabPane();

    private final Label lblTotalValue = new Label("0");
    private final Label lblWeakWordsValue = new Label("0");
    private final Label lblMasteredValue = new Label("0");
    private ComboBox<String> comboSort = new ComboBox<>();

    public VocabularyView() {
        this.setSpacing(20);
        this.setPadding(new Insets(10));
        this.setMaxWidth(950);

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label titleLabel = new Label("Dil Akademisi & Canlı Dashboard");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label subtitleLabel = new Label("Kelime skorlarınızı izleyin, zayıf kelimelerinizi güçlendirin.");
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        HBox actionButtonBox = new HBox(12);
        actionButtonBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnAddWord = new Button("➕ Add New Word");
        btnAddWord.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");

        Button btnImport = new Button("📥 CSV Import");
        btnImport.setStyle("-fx-background-color: #00e676; -fx-text-fill: #1a237e; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");

        actionButtonBox.getChildren().addAll(btnAddWord, btnImport);
        HBox.setHgrow(titleBox, javafx.scene.layout.Priority.ALWAYS);
        headerRow.getChildren().addAll(titleBox, actionButtonBox);
        this.getChildren().add(headerRow);

        HBox bentoDashboard = new HBox(16);
        bentoDashboard.setMaxWidth(Double.MAX_VALUE);
        VBox card1 = createStatCard("Toplam Kelime Haznesi", lblTotalValue, "#1a237e");
        VBox card2 = createStatCard("Kritik Seviye (Skoru < 0)", lblWeakWordsValue, "#ef4444");
        VBox card3 = createStatCard("Master Seviye (Skoru >= 20)", lblMasteredValue, "#10b981");
        bentoDashboard.getChildren().addAll(card1, card2, card3);
        this.getChildren().add(bentoDashboard);

        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        Label lblSort = new Label("Sıralama Ölçütü:");
        lblSort.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");

        comboSort.getItems().addAll(
                "En Yeni Eklenti (Varsayılan)",
                "En Eski Eklenti",
                "Alfabetik (A-Z)",
                "Alfabetik (Z-A)",
                "Puana Göre (Önce En Düşükler)",
                "Puana Göre (Önce En Yüksekler)"
        );
        comboSort.setValue("En Yeni Eklenti (Varsayılan)");
        comboSort.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-padding: 4;");
        comboSort.setOnAction(e -> buildMultiLanguageTabs());
        filterBar.getChildren().addAll(lblSort, comboSort);
        this.getChildren().add(filterBar);

        tabPane.setStyle("-fx-background-radius: 12; -fx-border-radius: 12;");
        this.getChildren().add(tabPane);

        buildMultiLanguageTabs();

        btnAddWord.setOnAction(e -> {
            if (MainLayout.getInstance() != null) {
                MainLayout.getInstance().setContent(new AddWordView());
            }
        });
    }

    private void buildMultiLanguageTabs() {
        tabPane.getTabs().clear();

        List<String> activeLanguages = AppSettings.getInstance().getActiveLanguages();
        if (activeLanguages == null || activeLanguages.isEmpty()) {
            activeLanguages = new ArrayList<>();
            activeLanguages.add("İngilizce");
        }

        int userId = AppSettings.getInstance().getCurrentUser() != null ? AppSettings.getInstance().getCurrentUser().getId() : 0;
        List<Word> allWords = wordDAO.getWordsByUserId(userId);

        int totalCount = allWords.size();
        int weakCount = 0;
        int masteredCount = 0;
        for (Word w : allWords) {
            int score = w.getMastery();
            if (score < 0) weakCount++;
            if (score >= 20) masteredCount++;
        }
        lblTotalValue.setText(String.valueOf(totalCount));
        lblWeakWordsValue.setText(String.valueOf(weakCount));
        lblMasteredValue.setText(String.valueOf(masteredCount));

        for (String lang : activeLanguages) {
            Tab langTab = new Tab(lang + " Odası 📚");
            langTab.setClosable(false);

            TableView<Word> table = new TableView<>();

            TableColumn<Word, String> engCol = new TableColumn<>("Word Text");
            engCol.setCellValueFactory(new PropertyValueFactory<>("wordText"));

            TableColumn<Word, String> trCol = new TableColumn<>("Meaning");
            trCol.setCellValueFactory(new PropertyValueFactory<>("translationText"));

            TableColumn<Word, Void> actionCol = new TableColumn<>("Actions");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btnListen = new Button("🔊");
                private final Button btnCheck = new Button("🔍 Verify");
                private final Button btnDelete = new Button("🗑 Sil");
                private final HBox pane = new HBox(8, btnListen, btnCheck, btnDelete);

                {
                    pane.setAlignment(Pos.CENTER);
                    // TAMİR EDİLDİ: Tüm hücreyi kaplayan o çirkin pembe arka plan söküldü!
                    pane.setStyle("-fx-background-color: transparent;");

                    btnListen.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #0d47a1; -fx-cursor: hand; -fx-background-radius: 6;");
                    btnCheck.setStyle("-fx-background-color: #e0f2f1; -fx-text-fill: #004d40; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 6;");

                    // TAMİR EDİLDİ: Silme butonu şık, kurumsal ve transparan bir hover mimarisine çekildi
                    btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border-color: #fee2e2; -fx-border-radius: 6; -fx-border-width: 1; -fx-padding: 4 8;");
                    btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 4 8;"));
                    btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border-color: #fee2e2; -fx-border-radius: 6; -fx-border-width: 1; -fx-padding: 4 8;"));

                    btnListen.setOnAction(e -> AudioService.playEnglishSound(getTableView().getItems().get(getIndex()).getWordText()));

                    btnCheck.setOnAction(e -> {
                        Word word = getTableView().getItems().get(getIndex());
                        btnCheck.setText("⌛ ..");
                        new Thread(() -> {
                            String aiResult = TranslationService.translateToTurkish(word.getWordText());
                            javafx.application.Platform.runLater(() -> {
                                btnCheck.setText("🔍 Verify");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veritabanın: " + word.getTranslationText() + "\nAI Doğrulama Sonucu: " + aiResult);
                                alert.setTitle("AI Verification");
                                alert.setHeaderText(null);
                                alert.showAndWait();
                            });
                        }).start();
                    });

                    btnDelete.setOnAction(e -> {
                        Word word = getTableView().getItems().get(getIndex());
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Silmek istediğinize emin misiniz?", ButtonType.YES, ButtonType.NO);
                        confirm.setHeaderText(null);
                        confirm.showAndWait();
                        if (confirm.getResult() == ButtonType.YES && wordDAO.deleteWord(word.getId())) {
                            buildMultiLanguageTabs();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setGraphic(null);
                    else setGraphic(pane);
                }
            });

            TableColumn<Word, Integer> scoreCol = new TableColumn<>("Öğrenme Skoru 📊");
            scoreCol.setCellValueFactory(new PropertyValueFactory<>("mastery"));
            scoreCol.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Integer score, boolean empty) {
                    super.updateItem(score, empty);
                    if (empty || score == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        setText(String.valueOf(score));
                        if (score < 0) {
                            setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-alignment: center;");
                        } else if (score >= 10) {
                            setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-alignment: center;");
                        } else {
                            setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-alignment: center;");
                        }
                    }
                }
            });

            engCol.prefWidthProperty().bind(table.widthProperty().multiply(0.24));
            trCol.prefWidthProperty().bind(table.widthProperty().multiply(0.24));
            actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.32));
            scoreCol.prefWidthProperty().bind(table.widthProperty().multiply(0.18));

            table.getColumns().addAll(engCol, trCol, actionCol, scoreCol);
            table.setPrefHeight(400);
            table.setStyle("-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #e4e7eb;");
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            ObservableList<Word> filteredList = FXCollections.observableArrayList();
            for (Word w : allWords) {
                String cat = w.getCategory();
                if ((cat != null && cat.equalsIgnoreCase(lang)) ||
                        (lang.equalsIgnoreCase("İngilizce") && (cat == null || cat.trim().isEmpty() || cat.equalsIgnoreCase("General") || cat.equalsIgnoreCase("Academic")))) {
                    filteredList.add(w);
                }
            }

            applySortingToData(filteredList);
            table.setItems(filteredList);

            if (filteredList.isEmpty()) {
                table.setPlaceholder(new Label(lang + " odası henüz boş. Kelime ekleyerek başlayın!"));
            }

            langTab.setContent(table);
            tabPane.getTabs().add(langTab);
        }
    }

    private void applySortingToData(ObservableList<Word> data) {
        String option = comboSort.getValue();
        if (option == null || data.isEmpty()) return;
        switch (option) {
            case "En Yeni Eklenti (Varsayılan)": FXCollections.sort(data, (w1, w2) -> Integer.compare(w2.getId(), w1.getId())); break;
            case "En Eski Eklenti": FXCollections.sort(data, Comparator.comparingInt(Word::getId)); break;
            case "Alfabetik (A-Z)": FXCollections.sort(data, (w1, w2) -> w1.getWordText().compareToIgnoreCase(w2.getWordText())); break;
            case "Alfabetik (Z-A)": FXCollections.sort(data, (w1, w2) -> w2.getWordText().compareToIgnoreCase(w1.getWordText())); break;
            case "Puana Göre (Önce En Düşükler)": FXCollections.sort(data, Comparator.comparingInt(Word::getMastery)); break;
            case "Puana Göre (Önce En Yüksekler)": FXCollections.sort(data, (w1, w2) -> Integer.compare(w2.getMastery(), w1.getMastery())); break;
        }
    }

    public void refreshData() {
        buildMultiLanguageTabs();
    }

    private void importCSVData(File file, String targetLanguage) {
        if (AppSettings.getInstance().getCurrentUser() == null) return;
        int userId = AppSettings.getInstance().getCurrentUser().getId();
        boolean isPremium = AppSettings.getInstance().isCurrentUserPremium();
        int importedCount = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String eng = tokens[0].trim();
                    String tr = tokens[1].trim();
                    if (wordDAO.addWord(userId, isPremium, eng, tr, 0, targetLanguage)) {
                        importedCount++;
                    }
                }
            }
            buildMultiLanguageTabs();
            new Alert(Alert.AlertType.INFORMATION, "Toplu yükleme tamamlandı! " + importedCount + " adet kelime [" + targetLanguage + "] odasına yüklendi.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage()).showAndWait();
        }
    }

    private VBox createStatCard(String title, Label lblValue, String borderColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + borderColor + " transparent transparent transparent; -fx-border-width: 4 0 0 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 4);");
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-font-weight: bold;");
        lblValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }
}