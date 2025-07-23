package com.gui.app.controller;
// ? File: FileController.java


import com.gui.app.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;
import com.gui.app.util.*;
import com.gui.app.session.*;
import com.gui.app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

public class FileController {
    @FXML private TextArea fileContentArea;
    @FXML private TextField fileNameField;
    @FXML private ListView<String> fileListView;


    private static final Logger logger = LoggerUtil.getLogger(FileController.class);
    private static final String BASE_URL = "http://localhost:8080";
    //private static final String BASE_URL = "http://host.docker.internal:8080";
     
@FXML private StackPane contentPane;

@FXML
private void showCreatePanel() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FileEditor.fxml"));
    //Parent panel = loader.load();

    FileEditorController controller = loader.getController();
    //controller.setMode("create", "", "");
       Stage stage = new Stage();
        stage.setTitle("File Management");
        stage.setScene(new Scene(loader.load()));
       stage.show();
//    contentPane.getChildren().setAll(panel);
}

@FXML
private void showUpdatePanel() throws IOException {
    String selected = fileListView.getSelectionModel().getSelectedItem();
    if (selected == null) return;

    String content = Files.readString(Paths.get("tempfiles", selected));
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FileEditor.fxml"));
//    Parent editor = loader.load();

    FileEditorController controller = loader.getController();
   // controller.setMode("update", selected, content);
      Stage stage = new Stage();
        stage.setTitle("File Management");
        stage.setScene(new Scene(loader.load()));
       stage.show();
//    contentPane.getChildren().setAll(editor);
}

@FXML
private void showSharePanel() throws IOException {
    FXMLLoader loader = FXMLLoader.load(getClass().getResource("/fxml/SharePanel.fxml"));
//    contentPane.getChildren().setAll(sharePanel);
      Stage stage = new Stage();
        stage.setTitle("File Management");
        stage.setScene(new Scene(loader.load()));
       stage.show();
}
 
//--------------------------------

    @FXML
    private void handleCreateFile() {
        String name = fileNameField.getText();
        String content = fileContentArea.getText();

        if (name.isEmpty()) {
            showAlert("File name is required.");
            return;
        }

        Path filePath = Paths.get("tempfiles", name);
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
            logger.info("Created file: " + name);
            
            FileUtils.chunkAndEncryptAndUpload(filePath.toFile(), "", BASE_URL+"/upload"); 
//            ChunkManager.splitAndDistribute(filePath.toFile());
            showAlert("File created and distributed successfully.");
        } catch (Exception e) {
            logger.severe("Error creating file: " + e.getMessage());
            showAlert("Failed to create file.");
        }
    }

    @FXML
    private void handleUpdateFile() {
       SessionManager session = SessionManager.getInstance();
       User currentUser = session.getCurrentUser();
       String filename = fileNameField.getText();
        try{
          if (!AclManager.canWrite(filename, currentUser.getUsername())) {
            showAlert("You do not have write permission.");
            return;
            }

           handleCreateFile();
        }catch(Exception e ){
         
         }
         // same logic to overwrite
    }

    @FXML
    private void handleDeleteFile() {
        String name = fileNameField.getText();
        Path filePath = Paths.get("tempfiles", name);
        try {
            Files.deleteIfExists(filePath);
            logger.info("Deleted file: " + name);
            showAlert("File deleted.");
        } catch (IOException e) {
            logger.warning("Delete failed: " + e.getMessage());
            showAlert("Delete failed.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

@FXML
private void initialize() {
    handleRefreshFileList();
    fileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            loadFile(newVal);
        }
    });
}

@FXML
private void handleRefreshFileList() {
    fileListView.getItems().clear();
    File folder = new File("tempfiles");
    if (folder.exists() && folder.isDirectory()) {
        for (File f : folder.listFiles()) {
            if (f.isFile()) {
                fileListView.getItems().add(f.getName());
            }
        }
    }
}

private void loadFile(String filename) {
    Path filePath = Paths.get("tempfiles", filename);
    try {
        String content = Files.readString(filePath);
        fileNameField.setText(filename);
        fileContentArea.setText(content);
    } catch (IOException e) {
        showAlert("Failed to load file.");
    }
}

}



