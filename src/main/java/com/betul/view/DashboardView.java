package com.betul.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox {
    public DashboardView() {
        this.setSpacing(16);
        this.setPadding(new Insets(24));

        Label titleLabel = new Label("Welcome to Betulingo Dashboard ✨");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");

        Label descLabel = new Label("Create your own dictionary and practice.");
        descLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        this.getChildren().addAll(titleLabel, descLabel);
    }
}