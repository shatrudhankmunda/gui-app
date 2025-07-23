package com.gui.app.controller;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.util.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.gui.app.util.SceneNavigator;
import com.gui.app.util.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.crypto.*;
import com.gui.app.session.*;
import com.gui.app.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import java.util.logging.*;


import com.gui.app.util.HttpUploader;

public class DashboardController {
    @FXML private Label welcomeLabel;

    @FXML private AnchorPane rootPane;

    @FXML private Button manageUsersBtn;

    private static final Logger logger = LoggerUtil.getLogger(DashboardController.class);

    private static final long TIMEOUT_MILLIS = 5 * 60 * 1000;

    private static final String BASE_URL = "http://localhost:8080";
//    private static final String BASE_URL = "http://host.docker.internal:8080";

    private String currentUser;
    private String currentRole;

    public void setUserSession(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;
        welcomeLabel.setText("Welcome, " + user + " (" + role + ")");
        
    }
    public void initialize() {
       SessionManager session = SessionManager.getInstance();
       User user = session.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
            manageUsersBtn.setVisible(user.getRole().equals("admin"));
            session.updateActivity();
            setupUserActivityListeners();
            startAutoLogoutTimer();
            } else {
            // Session expired
                SceneNavigator.loadScene("Cloud Load Balancer - Login", "Login.fxml");
        }
    }
private void setupUserActivityListeners() {
    rootPane.setOnMouseMoved(e -> SessionManager.getInstance().updateActivity());
    rootPane.setOnKeyPressed(e -> SessionManager.getInstance().updateActivity());
}
public void startAutoLogoutTimer() {
    Timeline checker = new Timeline(
        new KeyFrame(Duration.seconds(30), e -> {
            SessionManager session = SessionManager.getInstance();
            if (!session.isSessionActive(TIMEOUT_MILLIS)) {
                User user = session.getCurrentUser();
                if (user != null) {
                    logger.info("SESSION TIMEOUT: " + user.getUsername());
                }
                session.logout();
                SceneNavigator.loadScene("Cloud Load Balancer - Login", "Login.fxml");
            }
        })
    );
    checker.setCycleCount(Timeline.INDEFINITE);
    checker.play();
}
    @FXML
    private void handleLogout() {
        logger.info("LOGOUT: " + SessionManager.getInstance().getCurrentUser().getUsername());
        SessionManager.getInstance().logout();
        SceneNavigator.loadScene("Cloud Load Balancer - Login", "Login.fxml");
    }

    @FXML
    private void handleUpload() {
    logger.info("started handleUpload()==>");
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File("/host-files"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                logger.info("UPLOAD: " + file.getName() + " uploaded by " +
                              SessionManager.getInstance().getCurrentUser().getUsername());  
                 FileUtils.chunkAndEncryptAndUpload(file, currentUser, BASE_URL+"/upload");           
                 new Alert(Alert.AlertType.INFORMATION, "Upload successful!").show();
                 logger.info("completed handleUpload() !");
            } catch (Exception e) {
                logger.severe("ERROR: " + e.getMessage());
                new Alert(Alert.AlertType.ERROR, "Error during upload: " + e.getMessage()).show();
            }
        }
    }


   @FXML
    private void handleDownload() {
        logger.info("started handleDownload()==>");
        TextInputDialog dialog = new TextInputDialog("file.txt");
        dialog.setTitle("Download");
        dialog.setHeaderText("Enter the file name to download");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(fileName -> {
            try {
                logger.info("UPLOAD: " + fileName + " uploaded by " +
                            SessionManager.getInstance().getCurrentUser().getUsername());
                URL url = new URL(BASE_URL+"/download?username=" + currentUser + "&file=" + fileName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    InputStream input = conn.getInputStream();
                    byte[] encrypted = input.readAllBytes();
                    SecretKey key = FileUtils.generateKey("SuperSecureKey123"); // same as upload
                    byte[] decrypted = FileUtils.decryptChunk(encrypted, key);

                    FileChooser fileChooser = new FileChooser();
//                    fileChooser.setInitialDirectory(new File("/host-files"));
                    fileChooser.setInitialFileName(fileName);
                    File saveFile = fileChooser.showSaveDialog(new Stage());
                    if (saveFile != null) {
                        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                            fos.write(decrypted);
                        }
                    }
                    new Alert(Alert.AlertType.INFORMATION, "Download complete").show();
                   logger.info("completed handleDownload() ! ");
                } else {
                    logger.warning("File Not Found!!");
                    new Alert(Alert.AlertType.ERROR, "File not found").show();
                }

            } catch (Exception e) {
                logger.severe("ERROR: " + e.getMessage());
                new Alert(Alert.AlertType.ERROR, "Download failed: " + e.getMessage()).show();
            }
        });
    }

@FXML
private void handleShare() {
//    Dialog<Pair<String, String>> dialog = new Dialog<>();
//    dialog.setTitle("Share File");
//
//    // Inputs: file name + user to share with
//    TextField fileField = new TextField();
//    fileField.setPromptText("File name");
//    TextField userField = new TextField();
//    userField.setPromptText("Username");
//    ChoiceBox<String> permissionBox = new ChoiceBox<>();
//    permissionBox.getItems().addAll("read", "write");
//    permissionBox.setValue("read");
//
//    VBox content = new VBox(fileField, userField, permissionBox);
//    dialog.getDialogPane().setContent(content);
//    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//
//    dialog.setResultConverter(dialogButton -> {
//        if (dialogButton == ButtonType.OK) {
//            return new Pair<>(fileField.getText(), userField.getText() + "|" + permissionBox.getValue());
//        }
//        return null;
//    });
//
//    Optional<Pair<String, String>> result = dialog.showAndWait();
//
//    result.ifPresent(pair -> {
//        String fileName = pair.getKey();
//        String[] parts = pair.getValue().split("\\|");
//        String shareWith = parts[0];
//        String permission = parts[1];
//
//        try {
//            ACLUtils.shareFile(currentUser, fileName, shareWith, permission);
//            new Alert(Alert.AlertType.INFORMATION, "Shared successfully.").show();
//        } catch (Exception e) {
//            logger.severe("ERROR: " + e.getMessage());
//            new Alert(Alert.AlertType.ERROR, "Error sharing: " + e.getMessage()).show();
//        }
//    });
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Acl.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Share Files");
        stage.setScene(new Scene(loader.load()));

        AclController controller = loader.getController();
        controller.initialize();

        stage.show();
    } catch (IOException e) {
      logger.severe("ERROR: " + e.getMessage());
    }
     
}


    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Delete functionality coming soon");
        alert.show();
    }
@FXML
private void handleManageUsers() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminPanel.fxml"));
        Stage stage = new Stage();
        stage.setTitle("User Management");
        stage.setScene(new Scene(loader.load()));

        AdminController controller = loader.getController();
        controller.loadUserList();

        stage.show();
    } catch (IOException e) {
      logger.severe("ERROR: " + e.getMessage());
    }
}

@FXML
private void handleFileManager() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/File.fxml"));
        Stage stage = new Stage();
        stage.setTitle("File Management");
        stage.setScene(new Scene(loader.load()));

        FileController controller = loader.getController();
       

        stage.show();
    } catch (IOException e) {
      logger.severe("ERROR: " + e.getMessage());
    }
}

}
