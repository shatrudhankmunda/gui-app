package com.gui.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import com.gui.app.util.*;
import com.gui.app.controller.DashboardController;
import com.gui.app.model.User;
import com.gui.app.session.*;
import java.util.logging.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
          private static final Logger logger = LoggerUtil.getLogger(LoginController.class);

    @FXML
    private void handleLogin(ActionEvent event) throws IOException{
        String username = usernameField.getText();
        String password = passwordField.getText();
       User user = SQLiteHelper.validateUser(username, password);
           if (user != null) {
            SessionManager.getInstance().login(user);
            SessionStore.saveLogin(username);
            logger.info("LOGIN: " + username + " logged in.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            DashboardController controller = loader.getController();
            controller.initialize();

            stage.setScene(scene);
            stage.show();
       }else {
           logger.warning("LOGIN FAILED: Attempted login with username=" + username);
           Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credentials", ButtonType.OK);
           alert.showAndWait();
    }
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
//        Stage stage = (Stage) usernameField.getScene().getWindow();
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
//        stage.setTitle("Register");
//        stage.setScene(new Scene(loader.load()));
          SceneNavigator.loadScene("Cloud Load Balancer - Register", "Register.fxml");
    }
}
