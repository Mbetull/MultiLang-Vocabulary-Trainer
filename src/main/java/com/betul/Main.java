package com.betul.view;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            MainLayout mainLayout = MainLayout.getInstance();

            if (mainLayout != null) {
                mainLayout.setContent(new VocabularyView());
            }

            Scene scene = new Scene(mainLayout, 1200, 750);

            try {
                Image appIcon = new Image(getClass().getResourceAsStream("/icons/icon.png"));
                primaryStage.getIcons().add(appIcon);
            } catch (Exception e) {
                System.err.println("-> [Betulingo UI] İkon dosyası okunamadı veya bulunamadı: " + e.getMessage());
            }

            primaryStage.setTitle("Betulingo - Academic Excellence");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("-> [Betulingo Main] Uygulama başlatma krizi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}