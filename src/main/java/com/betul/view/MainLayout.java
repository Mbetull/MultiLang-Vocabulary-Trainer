package com.betul.view;

import com.betul.service.AppSettings;
import com.betul.service.NavigationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainLayout extends BorderPane {

    private static MainLayout instance;
    private final VBox contentArea = new VBox();

    public static MainLayout getInstance() {
        return instance;
    }

    public MainLayout() {
        instance = this;

        VBox sidebar = new VBox(16);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #f8fafc; -fx-border-color: transparent #e4e7eb transparent transparent; -fx-border-width: 1;");

        VBox brandBox = new VBox(4);
        brandBox.setAlignment(Pos.CENTER);
        Label lblLogo = new Label("🎓");
        lblLogo.setStyle("-fx-font-size: 28px; -fx-background-color: #1a237e; -fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 10;");
        Label lblTitle = new Label("Betulingo");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        brandBox.getChildren().addAll(lblLogo, lblTitle);
        sidebar.getChildren().add(brandBox);

        Button btnWords = new Button("📖  Kelimelerim");
        Button btnAddWord = new Button("➕  Kelime Ekle");
        Button btnPractice = new Button("🎯  Pratik Yap");
        Button btnSettings = new Button("⚙  Ayarlar");

        Button[] menuButtons = {btnWords, btnAddWord, btnPractice, btnSettings};
        for (Button btn : menuButtons) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 12; -fx-cursor: hand;");
        }

        btnWords.setOnAction(e -> setContent(new VocabularyView()));
        btnAddWord.setOnAction(e -> setContent(new AddWordView()));
        btnPractice.setOnAction(e -> setContent(new PracticeView()));


        btnSettings.setOnAction(e -> setContent(new ProfileView()));

        sidebar.getChildren().addAll(btnWords, btnAddWord, btnPractice, btnSettings);

        boolean isPremium = AppSettings.getInstance().isCurrentUserPremium();
        Label lblProBadge = new Label(isPremium ? "★ PRO MEMBER" : "🛡 STANDARD MEMBER");
        lblProBadge.setMaxWidth(Double.MAX_VALUE);
        lblProBadge.setAlignment(Pos.CENTER);
        String badgeStyle = isPremium ? "-fx-background-color: #e0f2fe; -fx-text-fill: #0284c7;" : "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;";
        lblProBadge.setStyle(badgeStyle + " -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 8;");

        VBox footerBox = new VBox(lblProBadge);
        footerBox.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(footerBox, javafx.scene.layout.Priority.ALWAYS);
        sidebar.getChildren().add(footerBox);

        this.setLeft(sidebar);

        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #f9f9f9;");

        contentArea.getChildren().add(new VocabularyView());
        this.setCenter(contentArea);
    }

    public void setContent(javafx.scene.Node newNode) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(newNode);
    }
}