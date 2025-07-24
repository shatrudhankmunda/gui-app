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
import javafx.stage.*;
import javafx.scene.layout.StackPane;
import com.gui.app.db.LocalFileManager;
import java.util.*; 

public class FileController {
    @FXML private TextArea fileContentArea;
    @FXML private TextField fileNameField;
    @FXML private ListView<String> fileListView;


    private static final Logger logger = LoggerUtil.getLogger(FileController.class);
    private static final String BASE_URL = "http://localhost:8080";
    //private static final String BASE_URL = "http://host.docker.internal:8080";
     
   private User user;

   public void setUser(User user){
     this.user = user;
   }

@FXML
private void showCreatePanel() throws IOException {
    openFileEditor("create", "", "");
}

@FXML
private void showUpdatePanel() throws Exception {
    String selected = fileListView.getSelectionModel().getSelectedItem();
    if (selected != null){
    if(!AclManager.canRead(selected, user.getUsername())){
        showAlert("You don’t have permission to read this file.");
        return;
     }
       String content = Files.readString(Paths.get("tempfiles", selected));
       openFileEditor("update", selected, content);
   }else{
     showAlert("Please select a file !!");
  }
}

@FXML
private void showSharePanel() throws IOException {
    String selectedFile = fileListView.getSelectionModel().getSelectedItem();
    if (selectedFile != null) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SharePanel.fxml"));
            Parent root = loader.load();

            SharePanelController controller = loader.getController();
            controller.setMode(selectedFile);

            Stage stage = new Stage();
            stage.setTitle("Share File");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }   
  }
    @FXML
    private void handleDeleteFile() {
         String filename = fileListView.getSelectionModel().getSelectedItem();
    if (filename == null) return;

    String username = user.getUsername();
    String role = user.getRole();

    // ✅ Only owner or admin can delete
    if (!AclManager.isOwner(username, filename) && !role.equalsIgnoreCase("admin")) {
        showAlert("Only the file owner or an admin can delete this file.");
        return;
    }

    boolean deleted = true;//DatabaseManager.deleteFile(filename);
    if (deleted) {
        LocalFileManager.deleteFileLocally(filename, username);
        handleRefreshFileList();
    } else {
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
}

@FXML
private void handleRefreshFileList() {
   List<String> allFiles = new ArrayList<>();

    // Cloud files from DB
    //allFiles.addAll(DatabaseManager.getAllFilenames());

    // Local files
    File localDir = new File("tempfiles");
    if (localDir.exists()) {
        File[] files = localDir.listFiles(); // or other filters
        if (files != null) {
            for (File file : files) {
                if (!allFiles.contains(file.getName())) {
                    allFiles.add(file.getName());
                }
            }
        }
    }

    fileListView.getItems().setAll(allFiles);
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

public void openFileEditor(String mode, String filename, String content) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FileEditor.fxml"));
        Parent root = loader.load();

        FileEditorController controller = loader.getController();
        controller.setMode(mode, filename, content);

        Stage stage = new Stage();
        stage.setTitle(mode.equals("create") ? "Create New File" : "Update File");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        stage.setOnHiding(e -> handleRefreshFileList());
        stage.showAndWait(); // Wait until this window is closed

    } catch (IOException e) {
        e.printStackTrace();
    }
}


}



