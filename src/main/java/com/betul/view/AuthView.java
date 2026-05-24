package com.betul.view;

import com.betul.dao.UserDAO;
import com.betul.model.User;
import com.betul.service.AppSettings;
import com.betul.service.NavigationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class AuthView extends VBox {
    private final UserDAO userDAO = new UserDAO();
    private boolean isLoginState = true;

    public AuthView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(24);
        this.setPadding(new Insets(32));
        this.setStyle("-fx-background-color: #f9f9f9;");

        VBox brandingBox = new VBox(8);
        brandingBox.setAlignment(Pos.CENTER);
        Label logoLabel = new Label("🎓");
        logoLabel.setStyle("-fx-font-size: 32px; -fx-background-color: #1a237e; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 12;");
        Label titleLabel = new Label("Betulingo");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label subtitleLabel = new Label("Academic Excellence & Cognitive Clarity");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        brandingBox.getChildren().addAll(logoLabel, titleLabel, subtitleLabel);

        VBox cardBox = new VBox(20);
        cardBox.setMaxWidth(400);
        cardBox.setPadding(new Insets(32));
        cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 16;");

        VBox nameGroup = new VBox(8);
        Label nameLabel = new Label("Full Name");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #454652; -fx-font-size: 13px;");
        TextField nameField = new TextField();
        nameField.setPromptText("Meryem Betül");
        nameField.getStyleClass().add("text-input");
        nameGroup.getChildren().addAll(nameLabel, nameField);

        nameGroup.setVisible(false);
        nameGroup.setManaged(false);

        VBox emailGroup = new VBox(8);
        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #454652; -fx-font-size: 13px;");
        TextField emailField = new TextField();
        emailField.setPromptText("name@academy.edu");
        emailField.getStyleClass().add("text-input");
        emailGroup.getChildren().addAll(emailLabel, emailField);

        VBox passwordGroup = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #454652; -fx-font-size: 13px;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        passwordField.getStyleClass().add("text-input");
        passwordGroup.getChildren().addAll(passwordLabel, passwordField);

        Button actionButton = new Button("Login");
        actionButton.getStyleClass().add("focus-button");
        actionButton.setMaxWidth(Double.MAX_VALUE);

        Hyperlink toggleLink = new Hyperlink("Don't have an account? Join the Academy");
        toggleLink.setStyle("-fx-text-fill: #006493; -fx-font-weight: bold;");

        cardBox.getChildren().addAll(nameGroup, emailGroup, passwordGroup, actionButton, toggleLink);

        VBox quoteBox = new VBox(6);
        quoteBox.setAlignment(Pos.CENTER);
        Label quoteLabel = new Label("\"The beautiful thing about learning is that no one can take it away from you.\"");
        quoteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b; -fx-text-alignment: center;");
        quoteLabel.setWrapText(true);
        quoteLabel.setMaxWidth(380);
        Label authorLabel = new Label("— B.B. King");
        authorLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        quoteBox.getChildren().addAll(quoteLabel, authorLabel);

        this.getChildren().addAll(brandingBox, cardBox, quoteBox);


        toggleLink.setOnAction(e -> {
            isLoginState = !isLoginState;
            if (isLoginState) {
                actionButton.setText("Login");
                toggleLink.setText("Don't have an account? Join the Academy");
                nameGroup.setVisible(false);
                nameGroup.setManaged(false);
            } else {
                actionButton.setText("Register & Join");
                toggleLink.setText("Already have an account? Login");
                nameGroup.setVisible(true);
                nameGroup.setManaged(true);
            }
        });

        Runnable performAuth = () -> {
            String email = emailField.getText().trim();
            String pass = passwordField.getText().trim();
            String fullName = nameField.getText().trim();

            if (email.isEmpty() || pass.isEmpty() || (!isLoginState && fullName.isEmpty())) {
                showAlert("Warning", "Please fill in all fields!");
                return;
            }

            if (isLoginState) {
                User user = userDAO.loginUser(email, pass);
                if (user != null) {
                    AppSettings.getInstance().setCurrentUser(user);
                    System.out.println("-> Login successful! Welcome " + user.getUsername());
                    NavigationService.getInstance().navigateTo(new MainLayout());
                } else {
                    showAlert("Error", "Invalid email or password!");
                }
            } else {
                boolean success = userDAO.registerUser(fullName, email, pass);
                if (success) {
                    showAlert("Success", "Account created successfully, " + fullName + "! You can login now.");
                    nameField.clear();
                    toggleLink.fire();
                } else {
                    showAlert("Error", "Registration failed! Email might be already in use.");
                }
            }
        };

        actionButton.setOnAction(e -> performAuth.run());

        nameField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) performAuth.run(); });
        emailField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) performAuth.run(); });
        passwordField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) performAuth.run(); });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}