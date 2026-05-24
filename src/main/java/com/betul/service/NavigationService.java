package com.betul.service;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class NavigationService {
    private static NavigationService instance;
    private Stage primaryStage;

    private NavigationService() {}

    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void navigateTo(Pane viewRoot) {
        if (primaryStage != null) {
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(viewRoot, 900, 650); // Sabit ideal pencere boyutu
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(viewRoot);
            }
            primaryStage.show();
        }
    }
}