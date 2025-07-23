package com.gui.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import com.gui.app.util.*;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister(ActionEvent event) throws IOException{
         String username = usernameField.getText();
    String password = passwordField.getText();
    String confirm = confirmPasswordField.getText();

    if (!password.equals(confirm)) {
        new Alert(Alert.AlertType.ERROR, "Passwords don't match").show();
        return;
    }

    boolean success = SQLiteHelper.registerUser(username, password, "standard");
    if (success) {
        new Alert(Alert.AlertType.INFORMATION, "Registration successful!").show();
        goToLogin(event);
    } else {
        new Alert(Alert.AlertType.ERROR, "Registration failed (duplicate username?)").show();
    }
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        SceneNavigator.loadScene("Cloud Load Balancer - Login", "Login.fxml");
    }
}
